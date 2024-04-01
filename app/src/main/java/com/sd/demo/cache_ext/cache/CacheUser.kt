package com.sd.demo.cache_ext.cache

import com.sd.lib.cache.ext.single.FSingleCache

object CacheUser : FSingleCache<UserModel>(UserModel::class.java) {
    override fun create(): UserModel {
        return UserModel("default", "default")
    }
}