package com.sd.lib.cache.ext.single

interface ISingleCache<T> {
    /**
     * 保存
     */
    fun put(model: T?): Boolean

    /**
     * 获取
     */
    fun get(): T?

    /**
     * 删除
     */
    fun remove()

    /**
     * 修改
     */
    fun modify(block: (cache: T) -> Unit): T?
}