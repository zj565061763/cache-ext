package com.sd.lib.cache.ext.multi

import kotlinx.coroutines.Dispatchers

interface IMultiCache<T> {
    /**
     * 保存
     */
    suspend fun put(key: String, model: T?): Boolean

    /**
     * 获取
     */
    suspend fun get(key: String): T?

    /**
     * 删除
     */
    suspend fun remove(key: String)

    /**
     * 编辑
     * @param block [Dispatchers.IO]上执行
     */
    suspend fun <R> edit(block: IMultiCache<T>.() -> R): R
}