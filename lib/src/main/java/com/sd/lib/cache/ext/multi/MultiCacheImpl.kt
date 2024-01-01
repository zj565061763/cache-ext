package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.FCache
import com.sd.lib.cache.ext.cacheEdit

class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = FCache.get(),
) : IMultiCache<T> {

    private val _cache = cache.cObjects(clazz)

    override suspend fun put(key: String, value: T?): Boolean {
        return edit {
            _cache.put(key, value)
        }
    }

    override suspend fun get(key: String): T? {
        return edit {
            _cache.get(key)
        }
    }

    override suspend fun remove(key: String) {
        edit {
            _cache.remove(key)
        }
    }

    override suspend fun keys(): Array<String> {
        return edit {
            _cache.keys()
        }
    }

    override suspend fun <R> edit(block: suspend IMultiCache<T>.() -> R): R {
        return cacheEdit {
            block()
        }
    }
}