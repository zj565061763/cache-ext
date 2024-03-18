package com.sd.demo.cache_ext

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.sd.demo.cache_ext.cache.CachesUser
import com.sd.demo.cache_ext.cache.UserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiCacheTest {

    @Test
    fun multiCacheFlowTest() = runBlocking {
        val defaultUser = UserModel("default", "default")
        assertEquals(defaultUser, CachesUser.get("default"))

        UserModel("1", "1").let { user ->
            assertEquals(true, CachesUser.put("1", user))
            assertEquals(user, CachesUser.get("1"))
            CachesUser.flow("1").test {
                assertEquals(user, awaitItem())
            }
        }

        CachesUser.remove("1")
        assertEquals(null, CachesUser.get("1"))
        CachesUser.flow("1").test {
            assertEquals(null, awaitItem())
        }
    }
}