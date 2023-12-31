package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : IMultiCache<T> {

    private val _cache = cache.cObjects(clazz)
    private val _mutex = Mutex()

    override suspend fun put(key: String, model: T?): Boolean {
        _mutex.withLock {
            return withContext(Dispatchers.IO) {
                _cache.put(key, model)
            }
        }
    }

    override suspend fun get(key: String): T? {
        _mutex.withLock {
            return withContext(Dispatchers.IO) {
                _cache.get(key)
            }
        }
    }

    override suspend fun remove(key: String) {
        _mutex.withLock {
            withContext(Dispatchers.IO) {
                _cache.remove(key)
            }
        }
    }

    override suspend fun <R> edit(block: suspend IMultiCache<T>.() -> R): R {
        _mutex.withLock {
            return block()
        }
    }
}