package com.sd.demo.cache_ext.cache

import com.sd.lib.cache.ext.FMultiCache

object CacheUsers : FMultiCache<UserModel>(UserModel::class.java) {
    override fun create(key: String): UserModel? {
        return if (key == "default") {
            UserModel(id = key, name = key)
        } else {
            null
        }
    }
}