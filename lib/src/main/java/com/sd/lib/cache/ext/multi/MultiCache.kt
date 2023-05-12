package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.FCache

abstract class MultiCache<T>(val clazz: Class<T>) : IMultiCache<T> {
    protected open val cache: Cache get() = FCache.disk()

    override fun put(key: String, model: T?): Boolean {
        return cache.cacheMultiObject(clazz).put(key, model)
    }

    override fun get(key: String): T? {
        return cache.cacheMultiObject(clazz).get(key)
    }

    override fun remove(key: String) {
        cache.cacheMultiObject(clazz).remove(key)
    }
}