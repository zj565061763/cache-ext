package com.sd.demo.cache_ext

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.demo.cache_ext.cache.CacheUser
import com.sd.demo.cache_ext.cache.UserModel
import com.sd.demo.cache_ext.databinding.SampleSingleCacheBinding
import com.sd.lib.coroutine.FScope

class SampleSingleCache : AppCompatActivity() {
    private val _binding by lazy { SampleSingleCacheBinding.inflate(layoutInflater) }

    private val _scope = FScope(lifecycleScope)
    private var _count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        _binding.btnChange.setOnClickListener {
            _scope.launch {
                _count++
                CacheUser.put(UserModel(_count.toString(), _count.toString()))
            }
        }
        _binding.btnRemove.setOnClickListener {
            _scope.launch {
                CacheUser.remove()
            }
        }

        _binding.btnRegister.setOnClickListener {
            _scope.cancel()
            _scope.launch {
                CacheUser.flow().collect {
                    _binding.tvUser.text = it.toString()
                }
            }
        }
        _binding.btnUnregister.setOnClickListener {
            _scope.cancel()
        }
    }
}