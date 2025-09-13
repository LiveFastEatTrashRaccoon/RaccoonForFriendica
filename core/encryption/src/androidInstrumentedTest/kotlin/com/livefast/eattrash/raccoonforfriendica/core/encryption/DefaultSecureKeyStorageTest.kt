package com.livefast.eattrash.raccoonforfriendica.core.encryption

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class DefaultSecureKeyStorageTest {

    private val sut = DefaultSecureKeyStorage(context = ApplicationProvider.getApplicationContext())

    @Before
    fun setup() {
        sut.reset()
    }

    @Test
    fun givenNotExistingEntry_whenGetKeyThenResultIsNull() {
        val result = sut.getKey(ALIAS)

        assertNull(result)
    }

    @Test
    fun givenDifferentAlias_whenGetKey_thenResultIsNull() {
        val fakeData = Random.nextBytes(10)
        sut.storeKey(ALIAS, fakeData)

        val result = sut.getKey("other-alias")

        assertNull(result)
    }

    @Test
    fun givenPreviouslyStoredEntry_whenGetKey_thenResultIsAsExpected() {
        val fakeData = Random.nextBytes(10)
        sut.storeKey(ALIAS, fakeData)

        val result = sut.getKey(ALIAS)

        assertArrayEquals(fakeData, result)
    }

    companion object {
        private const val ALIAS = "fake-alias"
    }
}
