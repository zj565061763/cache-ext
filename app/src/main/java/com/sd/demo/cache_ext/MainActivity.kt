package com.sd.demo.cache_ext

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.cache_ext.cache.CacheUser
import com.sd.demo.cache_ext.cache.CachesUser
import com.sd.demo.cache_ext.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val _binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val _scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        getFlowCaches()
    }

    private fun getCache() {
        _scope.launch {
            logMsg { "getCache start" }

            val user = CacheUser.get()
            logMsg { "getCache:$user" }

            logMsg { "getCache end" }
        }
    }

    private fun getCaches() {
        _scope.launch {
            logMsg { "getCaches start" }

            val user = CachesUser.get("id")
            logMsg { "getCaches:$user" }

            logMsg { "getCaches end" }
        }
    }

    private fun getFlowCaches() {
        _scope.launch {
            logMsg { "flow start" }

            CachesUser.flowOf("id").collect {
                logMsg { "flow $it" }
            }

            logMsg { "flow end" }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _scope.cancel()
    }
}


inline fun logMsg(block: () -> String) {
    Log.i("cache-ext-demo", block())
}