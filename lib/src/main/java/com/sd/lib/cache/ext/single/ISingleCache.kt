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
     * 修改，如果[block]返回值不为null，则将该返回值更新为缓存
     */
    fun modify(block: (cache: T) -> T): T?
}