package com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class DefaultEncryptionHelperTest {
    private val sut = DefaultEncryptionHelper()

    @Test
    fun whenEncrypt_thenResultIsNotNull() {
        val original = "Sample text"
        val encrypted = sut.encrypt(original)

        assertNotNull(encrypted)
    }

    @Test
    fun givenEmptyInput_whenEncrypt_thenResultIsNotNull() {
        val original = ""
        val encrypted = sut.encrypt(original)

        assertNotNull(encrypted)
    }

    @Test
    fun whenEncrypt_thenResultIsDifferentFromInput() {
        val original = "Sample text"
        val encrypted = sut.encrypt(original)?.toString(Charsets.UTF_8).orEmpty()

        assertNotEquals(original, encrypted)
    }

    @Test
    fun whenDecrypt_thenResultIsAsExpected() {
        val original = "Sample text"
        val encrypted = sut.encrypt(original)
        assertNotNull(encrypted)

        val result = sut.decrypt(encrypted)

        assertEquals(original, result)
    }

    @Test
    fun givenEncryptedFromOtherInstance_whenDecrypt_thenResultIsAsExpected() {
        val original = "Sample text"
        val encrypted = DefaultEncryptionHelper().encrypt(original)
        assertNotNull(encrypted)

        val result = sut.decrypt(encrypted)

        assertEquals(original, result)
    }

    @Test
    fun givenEncodedToString_whenDecodeFromString_thenResultIsAsExpected() {
        val original = sut.encrypt("Sample text")
        assertNotNull(original)
        val encoded = sut.encodeToString(original)

        val result = sut.decodeFromString(encoded)

        assertEquals(original.size, result.size)
        original.forEachIndexed { idx, value ->
            assertEquals(value, result[idx])
        }
    }

    @Test
    fun givenEncodedToString_whenDecrypt_thenResultIsAsExpected() {
        val original = "Sample text"
        val encrypted = sut.encrypt(original)
        assertNotNull(encrypted)
        val encryptedString = sut.encodeToString(encrypted)

        val input = sut.decodeFromString(encryptedString)
        val result = sut.decrypt(input)

        assertEquals(original, result)
    }
}
