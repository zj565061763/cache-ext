package com.sd.lib.cache.ext.single

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class FlowSingleCache<T>(clazz: Class<T>) : SingleCache<T>(clazz) {
    private val _flow = MutableSharedFlow<T>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _readonlyFlow by lazy {
        synchronized(clazz) {
            if (_flow.replayCache.isEmpty()) {
                syncFlowIfEmpty()
            }
        }
        _flow.asSharedFlow()
    }

    fun flow(): Flow<T> {
        return _readonlyFlow
    }

    final override fun get(): T? {
        synchronized(clazz) {
            return _readonlyFlow.replayCache.lastOrNull() ?: syncFlowIfEmpty()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    final override fun onCacheChanged(cache: T?) {
        super.onCacheChanged(cache)
        if (cache != null) {
            _flow.tryEmit(cache)
        } else {
            _flow.resetReplayCache()
        }
    }

    private fun syncFlowIfEmpty(): T? {
        return super.get()?.also {
            if (_flow.replayCache.isEmpty()) {
                _flow.tryEmit(it)
            }
        }
    }
}