package com.sd.lib.cache.ext.multi

import com.sd.lib.cache.Cache
import com.sd.lib.cache.FCache
import com.sd.lib.cache.ext.FMutableFlowStore
import com.sd.lib.cache.ext.cacheEdit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class MultiCache<T>(
    clazz: Class<T>,
    cache: Cache = FCache.get(),
) : IMultiCache<T> {

    private val _cache = cache.cObjects(clazz)
    private val _flowStore = FMutableFlowStore<MutableStateFlow<T?>>()

    final override suspend fun put(key: String, value: T?): Boolean {
        return edit {
            _cache.put(key, value).also {
                if (it) {
                    _flowStore.get(key)?.value = value
                }
            }
        }
    }

    final override suspend fun get(key: String): T? {
        return edit {
            _cache.get(key) ?: create(key)?.also { put(key, it) }
        }
    }

    final override suspend fun remove(key: String) {
        edit {
            _cache.remove(key)
            _flowStore.get(key)?.value = null
        }
    }

    final override suspend fun keys(): Array<String> {
        return edit {
            _cache.keys()
        }
    }

    final override suspend fun <R> edit(block: suspend IMultiCache<T>.() -> R): R {
        return cacheEdit {
            block()
        }
    }

    final override fun flowOf(key: String): Flow<T?> {
        return _flowStore.getOrPut(key) {
            MutableStateFlow<T?>(null).also { flow ->
                MainScope().launch {
                    flow.value = get(key)
                }
            }
        }
    }

    /**
     * 如果[get]方法未找到缓存，会尝试调用此方法创建缓存返回，[Dispatchers.IO]上执行
     */
    protected open fun create(key: String): T? = null
}