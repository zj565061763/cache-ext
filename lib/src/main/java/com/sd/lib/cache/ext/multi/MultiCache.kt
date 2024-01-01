package com.sd.lib.cache.ext.multi

import kotlinx.coroutines.Dispatchers

/**
 * 多缓存管理接口，所有方法均在[Dispatchers.IO]上执行
 */
interface IMultiCache<T> {
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
    suspend fun <R> edit(block: suspend IMultiCache<T>.() -> R): R
}