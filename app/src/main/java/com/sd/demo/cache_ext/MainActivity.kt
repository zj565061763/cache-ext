package com.sd.demo.cache_ext

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.cache_ext.cache.CacheUser
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
        _scope.launch {
            logMsg { "get start" }
            CacheUser.get()
            logMsg { "get end" }
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