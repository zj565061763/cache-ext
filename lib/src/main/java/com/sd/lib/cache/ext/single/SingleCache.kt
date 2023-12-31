package com.sd.lib.cache.ext.single

import kotlinx.coroutines.flow.Flow

interface ISingleCache<T> {
    /**
     * 保存
     */
    fun put(model: T?): Boolean

    /**
     * 如果缓存不存在则保存，如果缓存已存在则不保存
     */
    fun putIfAbsent(model: T?): Boolean

    /**
     * 获取
     */
    fun get(): T?

    /**
     * 删除
     */
    fun remove()

    /**
     * [block]返回值更新为缓存
     */
    fun modify(block: (cache: T?) -> T?): T?
}

interface ISingleFlowCache<T> : ISingleCache<T> {
    fun flow(): Flow<T?>
}