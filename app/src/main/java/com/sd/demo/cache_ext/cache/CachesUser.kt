package com.sd.demo.cache_ext.cache

import com.sd.lib.cache.ext.multi.MultiCache

object CachesUser : MultiCache<UserModel>(clazz = UserModel::class.java) {
    override fun create(key: String): UserModel {
        return UserModel(id = key, name = key)
    }
}