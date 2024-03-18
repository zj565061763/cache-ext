package com.sd.demo.cache_ext

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sd.demo.cache_ext.cache.CacheUsers
import com.sd.demo.cache_ext.cache.UserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiCacheTest {
    @Test
    fun test() = runBlocking {
        UserModel("default", "default").let { user ->
            CacheUsers.remove("default")
            assertEquals(user, CacheUsers.get("default"))
        }

        UserModel("1", "1").let { user ->
            assertEquals(true, CacheUsers.put("1", user))
            assertEquals(user, CacheUsers.get("1"))

            CacheUsers.remove("1")
            assertEquals(null, CacheUsers.get("1"))
        }

        UserModel("2", "2").let { user ->
            assertEquals(true, CacheUsers.put("2", user))
            assertEquals(user, CacheUsers.get("2"))

            CacheUsers.remove("2")
            assertEquals(null, CacheUsers.get("2"))
        }
    }

    @Test
    fun multiCacheFlowTest() = runBlocking {
        val defaultUser = UserModel("default", "default")
        assertEquals(defaultUser, CacheUsers.get("default"))

        UserModel("1", "1").let { user ->
            assertEquals(true, CacheUsers.put("1", user))
            assertEquals(user, CacheUsers.get("1"))
            CacheUsers.flow("1").test {
                assertEquals(user, awaitItem())
            }
        }

        CacheUsers.remove("1")
        assertEquals(null, CacheUsers.get("1"))
        CacheUsers.flow("1").test {
            assertEquals(null, awaitItem())
        }
    }
}