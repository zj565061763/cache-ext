package com.sd.lib.cache.ext.single


import com.sd.lib.cache.Cache
import com.sd.lib.cache.fCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class SingleCache<T>(
    val clazz: Class<T>,
    cache: Cache = fCache,
) : ISingleCache<T> {

    private val _cache = cache.cObject(clazz)

    override fun put(model: T?): Boolean {
        if (model == null) return false
        synchronized(clazz) {
            return _cache.put(model).also { result ->
                if (result) {
                    onCacheChanged(model)
                }
            }
        }
    }

    override fun putIfAbsent(model: T?): Boolean {
        if (model == null) return false
        return modify { cache ->
            if (cache == null) model else null
        } != null
    }

    override fun get(): T? {
        synchronized(clazz) {
            _cache.get()?.let { return it }
            return create()?.also {
                put(it)
            }
        }
    }

    override fun remove() {
        synchronized(clazz) {
            _cache.remove()
            onCacheChanged(null)
        }
    }

    override fun modify(block: (cache: T?) -> T?): T? {
        synchronized(clazz) {
            return block(get()).also {
                put(it)
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

abstract class SingleFlowCache<T>(
    clazz: Class<T>,
) : SingleCache<T>(clazz), ISingleFlowCache<T> {

    private val _flow: MutableStateFlow<T?> = MutableStateFlow(null)

    private val _readonlyFlow: StateFlow<T?> by lazy {
        get()
        _flow.asStateFlow()
    }

    final override fun flow(): Flow<T?> {
        return _readonlyFlow
    }

    override fun get(): T? {
        synchronized(clazz) {
            return _flow.value ?: super.get().also {
                _flow.value = it
            }
        }
    }

    final override fun onCacheChanged(cache: T?) {
        _flow.value = cache
    }
}