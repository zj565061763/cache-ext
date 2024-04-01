package com.sd.demo.cache_ext

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sd.demo.cache_ext.cache.CacheUser
import com.sd.demo.cache_ext.cache.UserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingleCacheTest {
    @Test
    fun test(): Unit = runBlocking {
        UserModel("default", "default").let { user ->
            CacheUser.remove()
            assertEquals(user, CacheUser.get())
        }

        UserModel("1", "1").let { user ->
            assertEquals(true, CacheUser.put(user))
            assertEquals(user, CacheUser.get())
        }

        UserModel("2", "2").let { user ->
            assertEquals(true, CacheUser.put(user))
            assertEquals(user, CacheUser.get())
        }

        UserModel("default", "default").let { user ->
            CacheUser.remove()
            assertEquals(user, CacheUser.get())
        }
    }

    @Test
    fun testFlow(): Unit = runBlocking {
        CacheUser.remove()
        CacheUser.flow().test {
            assertEquals(UserModel("default", "default"), awaitItem())

            CacheUser.put(UserModel("1", "1"))
            CacheUser.put(UserModel("1", "1"))
            assertEquals(UserModel("1", "1"), awaitItem())

            CacheUser.put(UserModel("2", "2"))
            CacheUser.put(UserModel("2", "2"))
            assertEquals(UserModel("2", "2"), awaitItem())

            CacheUser.remove()
            assertEquals(UserModel("default", "default"), awaitItem())
        }
    }
}