package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.FCache
import com.sd.lib.cache.ext.cacheEdit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class SingleCache<T>(
    clazz: Class<T>,
    cache: Cache = FCache.get(),
) : ISingleCache<T> {

    private val _cache = cache.cObject(clazz)

    override suspend fun put(value: T?): Boolean {
        return edit {
            _cache.put(value).also {
                if (it) {
                    onCacheChanged(value)
                }
            }
        }
    }

    override suspend fun get(): T? {
        return edit {
            _cache.get() ?: create()?.let {
                if (put(it)) it else null
            }
        }
    }

    override suspend fun remove() {
        edit {
            _cache.remove()
            onCacheChanged(null)
        }
    }

    final override suspend fun <R> edit(block: suspend ISingleCache<T>.() -> R): R {
        return cacheEdit {
            block()
        }
    }

    /**
     * 缓存对象变化回调，[Dispatchers.IO]上执行
     */
    protected open fun onCacheChanged(value: T?) {}

    /**
     * 如果[get]方法未找到缓存，会尝试调用此方法创建缓存返回，[Dispatchers.IO]上执行
     */
    protected open fun create(): T? = null
}

abstract class SingleFlowCache<T>(
    clazz: Class<T>,
    cache: Cache = FCache.get(),
) : SingleCache<T>(clazz, cache), ISingleFlowCache<T> {

    private val _flow: MutableSharedFlow<T?> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _readonlyFlow: SharedFlow<T?> by lazy {
        initFlow()
        _flow.asSharedFlow()
    }

    final override fun flow(): Flow<T?> {
        return _readonlyFlow
    }

    final override fun onCacheChanged(value: T?) {
        _flow.tryEmit(value)
    }

    private fun initFlow() {
        if (_flow.replayCache.isEmpty()) {
            MainScope().launch {
                if (_flow.replayCache.isEmpty()) {
                    _flow.tryEmit(super.get())
                }
            }
        }
    }
}