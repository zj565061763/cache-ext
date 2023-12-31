package com.sd.lib.cache.ext.single

import kotlinx.coroutines.flow.Flow

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
     * 编辑
     */
    suspend fun <R> edit(block: suspend ISingleCache<T>.() -> R): R
}

interface ISingleFlowCache<T> : ISingleCache<T> {
    fun flow(): Flow<T?>
}