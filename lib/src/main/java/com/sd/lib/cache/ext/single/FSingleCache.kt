package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.CacheDispatcher
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

open class FSingleCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : SingleCache<T> {

    private val _cache = cache.single(clazz)
    private var _flow: MutableStateFlow<T?>? = null

    override suspend fun put(value: T?): Boolean {
        return edit {
            _cache.put(value).also { put ->
                if (put) {
                    notifyCacheChanged(value)
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
            if (get() == null) {
                notifyCacheChanged(null)
            }
        }
    }

    override suspend fun <R> edit(block: suspend () -> R): R {
        return withContext(CacheDispatcher) { block() }
    }

    override suspend fun flow(): Flow<T?> {
        _flow?.let { return it.asStateFlow() }
        return edit {
            _flow ?: MutableStateFlow(get()).also {
                _flow = it
            }
        }.asStateFlow()
    }

    private fun notifyCacheChanged(value: T?) {
        _flow?.value = value
    }

    /**
     * 创建默认缓存对象，[Dispatchers.IO]上执行
     */
    protected open fun create(): T? = null
}