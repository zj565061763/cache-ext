package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.cacheEdit
import com.sd.lib.cache.fCache

class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : IMultiCache<T> {

    private val _cache = cache.cObjects(clazz)

    override suspend fun put(key: String, model: T?): Boolean {
        return edit {
            _cache.put(key, model)
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

    override suspend fun <R> edit(block: suspend IMultiCache<T>.() -> R): R {
        return cacheEdit {
            block()
        }
    }
}