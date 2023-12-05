package com.sd.lib.cache.ext.single

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class FlowSingleCache<T>(clazz: Class<T>) : SingleCache<T>(clazz) {
    private val _flow = MutableSharedFlow<T>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    fun flow(): Flow<T> {
        return _flow
    }

    final override fun get(): T? {
        synchronized(clazz) {
            return _flow.replayCache.lastOrNull() ?: super.get()?.also {
                _flow.tryEmit(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    final override fun onCacheChanged(cache: T?) {
        synchronized(clazz) {
            if (cache != null) {
                _flow.tryEmit(cache)
            } else {
                _flow.resetReplayCache()
            }
        }
    }
}