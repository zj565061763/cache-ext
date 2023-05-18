package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.FCache

abstract class SingleCache<T>(val clazz: Class<T>) : ISingleCache<T> {
    protected open val cache: Cache get() = FCache.disk()

    override fun put(model: T?): Boolean {
        if (model == null) return false
        synchronized(clazz) {
            return cache.cacheObject().put(model).also { result ->
                if (result) {
                    onCacheChanged(model)
                }
            }
        }
    }

    override fun get(): T? {
        synchronized(clazz) {
            val result = cache.cacheObject().get(clazz)
            if (result != null) return result
            return create()?.also {
                put(it)
            }
        }
    }

    override fun remove() {
        synchronized(clazz) {
            cache.cacheObject().remove(clazz)
            onCacheChanged(null)
        }
    }

    override fun modify(block: (cache: T) -> T?): T? {
        synchronized(clazz) {
            val cache = get() ?: return null
            val modify = block(cache)
            return if (modify != null) {
                put(modify)
                modify
            } else {
                cache
            }
        }
    }

    /**
     * 缓存对象变化回调
     */
    protected open fun onCacheChanged(cache: T?) {
    }

    /**
     * 如果[get]方法未找到缓存，则会尝试调用此方法创建缓存返回
     */
    protected open fun create(): T? {
        return null
    }
}