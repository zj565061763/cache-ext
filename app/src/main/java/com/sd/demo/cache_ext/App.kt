package com.sd.demo.cache_ext

import android.app.Application
import com.sd.lib.cache.CacheConfig

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CacheConfig.init(
            CacheConfig.Builder().build(this)
        )
    }
}