package com.sd.demo.cache_ext

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sd.demo.cache_ext.cache.CacheUser
import com.sd.demo.cache_ext.cache.CachesUser
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
        val defaultUser = UserModel("default", "default")
        assertEquals(defaultUser, CacheUser.get())

        UserModel("1", "1").let { user ->
            assertEquals(true, CacheUser.put(user))
            assertEquals(user, CacheUser.get())
            CacheUser.flow().test {
                assertEquals(user, awaitItem())
            }
        }

        CacheUser.remove()
        assertEquals(defaultUser, CacheUser.get())
        CacheUser.flow().test {
            assertEquals(defaultUser, awaitItem())
        }
    }

    @Test
    fun multiCacheFlowTest() = runBlocking {
        val defaultUser = UserModel("default", "default")
        assertEquals(defaultUser, CachesUser.get("default"))

        UserModel("1", "1").let { user ->
            assertEquals(true, CachesUser.put("1", user))
            assertEquals(user, CachesUser.get("1"))
            CachesUser.flowOf("1").test {
                assertEquals(user, awaitItem())
            }
        }
    }
}