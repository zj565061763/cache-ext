package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : IMultiCache<T> {

    private val _cache = cache.cObjects(clazz)

    override suspend fun put(key: String, model: T?): Boolean {
        return withContext(Dispatchers.IO) {
            synchronized(this@MultiCache) {
                _cache.put(key, model)
            }
        }
    }

    override suspend fun get(key: String): T? {
        return withContext(Dispatchers.IO) {
            synchronized(this@MultiCache) {
                _cache.get(key)
            }
        }
    }

    override suspend fun remove(key: String) {
        withContext(Dispatchers.IO) {
            synchronized(this@MultiCache) {
                _cache.remove(key)
            }
        }
    }

    override suspend fun <R> edit(block: IMultiCache<T>.() -> R): R {
        return withContext(Dispatchers.IO) {
            synchronized(this@MultiCache) {
                block()
            }
        }
    }
}