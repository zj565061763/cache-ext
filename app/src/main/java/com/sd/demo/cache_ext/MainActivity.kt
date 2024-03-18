package com.sd.demo.cache_ext

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.cache_ext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val _binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        _binding.btnSampleSingleCache.setOnClickListener {
            startActivity(Intent(this, SampleSingleCache::class.java))
        }
        _binding.btnSampleMultiCache.setOnClickListener {
            startActivity(Intent(this, SampleMultiCache::class.java))
        }
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("cache-ext-demo", block())
}