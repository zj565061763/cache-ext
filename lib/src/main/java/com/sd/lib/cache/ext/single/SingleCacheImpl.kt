package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

abstract class SingleCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : ISingleCache<T> {

    private val _cache = cache.cObject(clazz)
    private val _mutex = Mutex()

    override suspend fun put(model: T?): Boolean {
        _mutex.withLock {
            return withContext(Dispatchers.IO) {
                _cache.put(model)
            }.also {
                if (it) {
                    onCacheChanged(model)
                }
            }
        }
    }

    override suspend fun putIfAbsent(model: T?): Boolean {
        _mutex.withLock {
            return if (get() == null) put(model) else false
        }
    }

    override suspend fun get(): T? {
        _mutex.withLock {
            withContext(Dispatchers.IO) {
                _cache.get()
            }?.let { return it }

            return create()?.also {
                put(it)
            }
        }
    }

    override suspend fun remove() {
        _mutex.withLock {
            withContext(Dispatchers.IO) {
                _cache.remove()
            }
            onCacheChanged(null)
        }
    }

    final override suspend fun <R> edit(block: suspend ISingleCache<T>.() -> R): R {
        _mutex.withLock {
            return block()
        }
    }

    /**
     * 缓存对象变化回调
     */
    protected open fun onCacheChanged(cache: T?) {}

    /**
     * 如果[get]方法未找到缓存，则会尝试调用此方法创建缓存返回
     */
    protected open fun create(): T? = null
}

abstract class SingleFlowCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : SingleCache<T>(clazz, cache), ISingleFlowCache<T> {

    private val _flow: MutableStateFlow<T?> = MutableStateFlow(null)
    private val _readonlyFlow: StateFlow<T?> = _flow.asStateFlow()

    final override fun flow(): Flow<T?> {
        return _readonlyFlow
    }

    override suspend fun get(): T? {
        return edit {
            _flow.value ?: super.get().also {
                _flow.value = it
            }
        }
    }

    final override fun onCacheChanged(cache: T?) {
        _flow.value = cache
    }

    init {
        MainScope().launch {
            _flow.value = super.get()
        }
    }
}