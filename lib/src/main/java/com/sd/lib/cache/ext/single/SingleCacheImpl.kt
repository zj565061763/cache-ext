package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.CacheDispatcher
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class SingleCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : ISingleCache<T> {

    private val _cache = cache.single(clazz)

    override suspend fun put(value: T?): Boolean {
        return edit {
            _cache.put(value).also { put ->
                if (put) {
                    onCacheChanged(value)
                }
            }
        }
    }

    override suspend fun get(): T? {
        return edit {
            _cache.get() ?: create()?.let { create ->
                if (put(create)) create else null
            }
        }
    }

    override suspend fun remove() {
        edit {
            _cache.remove()
            onCacheChanged(null)
        }
    }

    override suspend fun <R> edit(block: suspend () -> R): R {
        return withContext(CacheDispatcher) { block() }
    }

    /**
     * 缓存对象变化回调，[Dispatchers.IO]上执行
     */
    protected open fun onCacheChanged(value: T?) = Unit

    /**
     * 如果[get]方法未找到缓存，会尝试调用此方法创建缓存返回，[Dispatchers.IO]上执行
     */
    protected open fun create(): T? = null
}

open class SingleFlowCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : SingleCache<T>(clazz, cache), ISingleFlowCache<T> {

    private val _flow: MutableStateFlow<T?> = MutableStateFlow(null)

    private val _readonlyFlow: StateFlow<T?> by lazy {
        MainScope().launch {
            if (_flow.value == null) {
                _flow.value = super.get()
            }
        }
        _flow.asStateFlow()
    }

    final override fun flow(): Flow<T?> {
        return _readonlyFlow
    }

    override fun onCacheChanged(value: T?) {
        _flow.value = value
    }
}