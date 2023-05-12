package com.sd.lib.cache.ext.multi

interface IMultiCache<T> {
    /**
     * 保存
     */
    fun put(key: String, model: T?): Boolean

    /**
     * 获取
     */
    fun get(key: String): T?

    /**
     * 删除
     */
    fun remove(key: String)
}