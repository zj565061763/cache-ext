package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.CacheDispatcher
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

open class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : IMultiCache<T> {

    private val _cache = cache.multi(clazz)

    override suspend fun put(key: String, value: T?): Boolean {
        return edit {
            _cache.put(key, value).also { put ->
                if (put) {
                    onCacheChanged(key, value)
                }
            }
        }
    }

    override suspend fun get(key: String): T? {
        return edit {
            _cache.get(key) ?: create(key)?.takeIf { put(key, it) }
        }
    }

    override suspend fun remove(key: String) {
        edit {
            _cache.remove(key)
            val create = create(key)
            if (create == null) {
                onCacheChanged(key, null)
            } else {
                put(key, create)
            }
        }
    }

    override suspend fun <R> edit(block: suspend () -> R): R {
        return withContext(CacheDispatcher) { block() }
    }

    /**
     * 缓存对象变化回调，[Dispatchers.IO]上执行
     */
    protected open fun onCacheChanged(key: String, value: T?) = Unit

    /**
     * 创建默认缓存对象，[Dispatchers.IO]上执行
     */
    protected open fun create(key: String): T? = null
}

open class MultiFlowCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : MultiCache<T>(clazz, cache), IMultiFlowCache<T> {

    private val _flows: MutableMap<String, WeakRef<MutableStateFlow<T?>>> = hashMapOf()
    private val _refQueue = ReferenceQueue<MutableStateFlow<T?>>()

    final override suspend fun flow(key: String): Flow<T?> {
        return edit {
            val flow = _flows[key]?.get() ?: kotlin.run {
                releaseRef()
                MutableStateFlow(get(key)).also {
                    _flows[key] = WeakRef(
                        referent = it,
                        queue = _refQueue,
                        key = key,
                    )
                }
            }
            flow.asStateFlow()
        }
    }

    override fun onCacheChanged(key: String, value: T?) {
        _flows[key]?.get()?.value = value
    }

    private fun releaseRef() {
        while (true) {
            val ref = _refQueue.poll() ?: return
            check(ref is WeakRef)
            _flows.remove(ref.key)
        }
    }

    private class WeakRef<T>(
        referent: T,
        queue: ReferenceQueue<in T>,
        val key: String,
    ) : WeakReference<T>(referent, queue)
}