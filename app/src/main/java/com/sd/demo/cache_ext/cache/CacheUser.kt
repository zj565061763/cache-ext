package com.sd.demo.cache_ext.cache

import com.sd.lib.cache.ext.single.SingleFlowCache

object CacheUser : SingleFlowCache<UserModel>(UserModel::class.java) {
    override fun create(): UserModel {
        return UserModel("default", "default")
    }
}