package com.sd.demo.cache_ext

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sd.demo.cache_ext.cache.CacheUser
import com.sd.demo.cache_ext.cache.UserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SingleCacheTest {
    @Test
    fun test() = runBlocking {
        CacheUser.remove()

        UserModel("default", "default").let { user ->
            assertEquals(user, CacheUser.get())
        }

        UserModel("1", "1").let { user ->
            assertEquals(true, CacheUser.put(user))
            assertEquals(user, CacheUser.get())
        }
    }

    @Test
    fun testFlow() = runBlocking {
        CacheUser.remove()

        CacheUser.flow().test {
            assertEquals(UserModel("default", "default"), awaitItem())

            CacheUser.put(UserModel("default", "default"))
            CacheUser.put(UserModel("1", "1"))
            CacheUser.put(UserModel("1", "1"))
            CacheUser.put(UserModel("2", "2"))
            CacheUser.put(UserModel("2", "2"))

            assertEquals(UserModel("1", "1"), awaitItem())
            assertEquals(UserModel("2", "2"), awaitItem())
        }
    }
}