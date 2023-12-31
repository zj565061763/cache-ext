package com.sd.lib.cache.ext.multi

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
     */
    suspend fun <R> edit(block: suspend IMultiCache<T>.() -> R): R
}
