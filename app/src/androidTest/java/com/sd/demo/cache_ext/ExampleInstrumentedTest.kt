package com.sd.demo.cache_ext

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sd.demo.cache_ext.cache.CacheUser
import com.sd.demo.cache_ext.cache.UserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun singleCacheFlowTest() = runBlocking {
        checkNotNull(CacheUser.get()).let {
            assertEquals("default", it.id)
            assertEquals("default", it.name)
        }

        val user = UserModel("1", "1")
        assertEquals(true, CacheUser.put(user))

        checkNotNull(CacheUser.get()).let { cache ->
            assertEquals("1", cache.id)
            assertEquals("1", cache.name)
            assertEquals(cache, user)
        }

        CacheUser.flow().test {
            val item = checkNotNull(awaitItem())
            assertEquals(item, user)
            cancelAndIgnoreRemainingEvents()
        }
    }
}