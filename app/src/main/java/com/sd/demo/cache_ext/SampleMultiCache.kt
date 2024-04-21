package com.sd.demo.cache_ext

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.demo.cache_ext.cache.CacheUsers
import com.sd.demo.cache_ext.cache.UserModel
import com.sd.demo.cache_ext.databinding.SampleMultiCacheBinding
import com.sd.lib.coroutines.FScope

class SampleMultiCache : AppCompatActivity() {
    private val _binding by lazy { SampleMultiCacheBinding.inflate(layoutInflater) }

    private val key = "key"

    private val _scope = FScope(lifecycleScope)
    private var _count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        _binding.btnChangeUser.setOnClickListener {
            changeUser()
        }
        _binding.btnRemoveUser.setOnClickListener {
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
            CacheUsers.put(key, UserModel(_count.toString(), _count.toString()))
        }
    }

    private fun removeUser() {
        _scope.launch {
            CacheUsers.remove(key)
        }
    }

    private fun register() {
        unregister()
        _scope.launch {
            CacheUsers.flow(key).collect {
                _binding.tvUser.text = it.toString()
            }
        }
    }

    private fun unregister() {
        _scope.cancel()
    }
}