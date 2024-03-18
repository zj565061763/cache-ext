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
            changeUser()
        }
        _binding.btnRemove.setOnClickListener {
            removeUser()
        }
        _binding.btnRegister.setOnClickListener {
            register()
        }
        _binding.btnUnregister.setOnClickListener {
            unregister()
        }

        register()
    }

    private fun changeUser() {
        _scope.launch {
            _count++
            CacheUser.put(UserModel(_count.toString(), _count.toString()))
        }
    }

    private fun removeUser() {
        _scope.launch {
            CacheUser.remove()
        }
    }

    private fun register() {
        unregister()
        _scope.launch {
            CacheUser.flow().collect {
                _binding.tvUser.text = it.toString()
            }
        }
    }

    private fun unregister() {
        _scope.cancel()
    }
}