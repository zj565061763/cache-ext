package com.sd.lib.cache.ext.single

import com.sd.lib.cache.Cache
import com.sd.lib.cache.ext.CacheDispatcher
import com.sd.lib.cache.fCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * 单缓存管理接口，所有方法均在[Dispatchers.IO]上执行，并发为1
 */
interface SingleCache<T> {
    /**
     * 保存
     */
    suspend fun put(value: T?): Boolean

    /**
     * 获取
     */
    suspend fun get(): T?

    /**
     * 删除
     */
    suspend fun remove()

    /**
     * 编辑，[block]为原子性操作
     */
    suspend fun <R> edit(block: suspend () -> R): R

    suspend fun flow(): Flow<T?>
}

open class FSingleCache<T>(
    clazz: Class<T>,
    cache: Cache = fCache,
) : SingleCache<T> {

    private val _cache = cache.single(clazz)
    private var _flow: MutableStateFlow<T?>? = null

    override suspend fun put(value: T?): Boolean {
        return edit {
            _cache.put(value).also { put ->
                if (put) {
                    notifyCacheChanged(value)
                }
            }
        }
    }

    override suspend fun get(): T? {
        return edit {
            _cache.get() ?: create()?.takeIf { put(it) }
        }
    }

    override suspend fun remove() {
        edit {
            _cache.remove()
            if (get() == null) {
                notifyCacheChanged(null)
            }
        }
    }

    final override suspend fun <R> edit(block: suspend () -> R): R {
        return withContext(CacheDispatcher) { block() }
    }

    override suspend fun flow(): Flow<T?> {
        val flow = _flow ?: edit {
            _flow ?: MutableStateFlow(get()).also {
                _flow = it
            }
        }
        return flow.asStateFlow()
    }

    private fun notifyCacheChanged(value: T?) {
        _flow?.value = value
    }

    /**
     * 创建默认缓存对象，[Dispatchers.IO]上执行
     */
    protected open fun create(): T? = null
}