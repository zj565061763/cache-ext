package com.sd.lib.cache.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
private val CacheDispatcher = Dispatchers.IO.limitedParallelism(1)

internal suspend fun <T> editCache(block: suspend () -> T): T {
    return withContext(CacheDispatcher) { block() }
}