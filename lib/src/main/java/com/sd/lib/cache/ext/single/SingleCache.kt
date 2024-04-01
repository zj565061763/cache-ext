package com.sd.lib.cache.ext.single

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * 单缓存管理接口，所有方法均在[Dispatchers.IO]执行，并发为1
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