package com.sd.lib.cache.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
internal val CacheDispatcher = Dispatchers.IO.limitedParallelism(1)