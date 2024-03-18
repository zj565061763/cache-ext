package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.CacheDispatcher
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
            _cache.get() ?: create()?.takeIf { put(it) }
        }
    }

    override suspend fun remove() {
        edit {
            _cache.remove()
            val create = create()
            if (create == null) {
                onCacheChanged(null)
            } else {
                put(create)
            }
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
     * 创建默认缓存对象，[Dispatchers.IO]上执行
     */
    protected open fun create(): T? = null
}

open class SingleFlowCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : SingleCache<T>(clazz, cache), ISingleFlowCache<T> {

    private val _flow: MutableSharedFlow<T?> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    final override suspend fun flow(): Flow<T?> {
        return edit {
            if (_flow.replayCache.isEmpty()) {
                _flow.tryEmit(get())
            }
            _flow.distinctUntilChanged()
        }
    }

    override fun onCacheChanged(value: T?) {
        _flow.tryEmit(value)
    }
}