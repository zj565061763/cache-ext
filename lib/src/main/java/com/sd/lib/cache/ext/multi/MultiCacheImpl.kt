package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.CacheDispatcher
import com.sd.lib.cache.ext.FMutableFlowStore
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : IMultiCache<T> {

    private val _cache = cache.multi(clazz)
    private val _flowStore = FMutableFlowStore<MutableSharedFlow<T?>>()

    override suspend fun put(key: String, value: T?): Boolean {
        return edit {
            _cache.put(key, value).also { put ->
                if (put) {
                    _flowStore.get(key)?.tryEmit(value)
                }
            }
        }
    }

    override suspend fun get(key: String): T? {
        return edit {
            _cache.get(key) ?: create(key)?.let { create ->
                if (put(key, create)) create else null
            }
        }
    }

    override suspend fun remove(key: String) {
        edit {
            _cache.remove(key)
            _flowStore.get(key)?.tryEmit(null)
        }
    }

    override suspend fun <R> edit(block: suspend () -> R): R {
        return withContext(CacheDispatcher) { block() }
    }

    final override suspend fun flow(key: String): Flow<T?> {
        return _flowStore.getOrPut(key) {
            MutableSharedFlow<T?>(
                replay = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            ).also { flow ->
                MainScope().launch {
                    flow.tryEmit(get(key))
                }
            }
        }
    }

    /**
     * 如果[get]方法未找到缓存，会尝试调用此方法创建缓存返回，[Dispatchers.IO]上执行
     */
    protected open fun create(key: String): T? = null
}