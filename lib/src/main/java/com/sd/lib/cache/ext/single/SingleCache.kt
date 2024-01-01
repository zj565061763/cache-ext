package com.sd.lib.cache.ext.single

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * 单缓存管理接口，所有方法均在[Dispatchers.IO]上执行
 */
interface ISingleCache<T> {
    /**
     * 保存
     */
    suspend fun put(model: T?): Boolean

    /**
     * 如果缓存不存在则保存，如果缓存已存在则不保存
     */
    suspend fun putIfAbsent(model: T?): Boolean

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
    suspend fun <R> edit(block: suspend ISingleCache<T>.() -> R): R
}

interface ISingleFlowCache<T> : ISingleCache<T> {
    fun flow(): Flow<T?>
}