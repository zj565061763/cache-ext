package com.sd.lib.cache.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

internal suspend fun <T> cacheEdit(block: suspend () -> T): T {
    return withContext(CacheContext) {
        block()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private val CacheContext = Dispatchers.IO.limitedParallelism(1)