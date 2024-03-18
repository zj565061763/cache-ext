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
}

interface ISingleFlowCache<T> : ISingleCache<T> {
    fun flow(): Flow<T?>
}