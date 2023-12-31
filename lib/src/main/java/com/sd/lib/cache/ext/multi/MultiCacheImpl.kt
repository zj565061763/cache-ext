package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.fCache

class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : IMultiCache<T> {

    private val _cache = cache.cObjects(clazz)

    override fun put(key: String, model: T?): Boolean {
        return _cache.put(key, model)
    }

    override fun get(key: String): T? {
        return _cache.get(key)
    }

    override fun remove(key: String) {
        _cache.remove(key)
    }
}