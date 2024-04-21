package com.sd.lib.cache.ext

import com.sd.lib.cache.Cache
import com.sd.lib.cache.FCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * 多缓存管理接口，所有方法均在[Dispatchers.IO]上执行，并发为1
 */
interface MultiCache<T> {
    /**
     * 保存
     */
    suspend fun put(key: String, value: T?): Boolean

    /**
     * 获取
     */
    suspend fun get(key: String): T?

    /**
     * 删除
     */
    suspend fun remove(key: String)

    /**
     * 编辑，[block]为原子性操作
     */
    suspend fun <R> edit(block: suspend () -> R): R

    /**
     * [key]对应的缓存[Flow]
     */
    suspend fun flow(key: String): Flow<T?>
}

open class FMultiCache<T>(
    clazz: Class<T>,
    cache: Cache = FCache.getDefault(),
) : MultiCache<T> {

    private val _cache = cache.multi(clazz)

    override suspend fun put(key: String, value: T?): Boolean {
        return edit {
            _cache.put(key, value).also { put ->
                if (put) {
                    notifyCacheChanged(key, value)
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
            if (get(key) == null) {
                notifyCacheChanged(key, null)
            }
        }
    }

    final override suspend fun <R> edit(block: suspend () -> R): R {
        return withContext(CacheDispatcher) { block() }
    }

    private val _flows: MutableMap<String, WeakRef<MutableStateFlow<T?>>> = mutableMapOf()
    private val _refQueue = ReferenceQueue<MutableStateFlow<T?>>()

    override suspend fun flow(key: String): Flow<T?> {
        return edit {
            releaseRef()
            _flows[key]?.get() ?: MutableStateFlow(get(key)).also { instance ->
                _flows[key] = WeakRef(
                    referent = instance,
                    queue = _refQueue,
                    key = key,
                )
            }
        }.asStateFlow()
    }

    private fun notifyCacheChanged(key: String, value: T?) {
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

    /**
     * 创建默认缓存对象，[Dispatchers.IO]上执行
     */
    protected open fun create(key: String): T? = null
}