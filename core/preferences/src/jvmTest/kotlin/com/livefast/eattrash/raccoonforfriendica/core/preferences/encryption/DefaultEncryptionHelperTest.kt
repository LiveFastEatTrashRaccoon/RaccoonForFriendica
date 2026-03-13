package com.livefast.eattrash.raccoonforfriendica.core.preferences.encryption

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class DefaultEncryptionHelperTest {

    private val sut = DefaultEncryptionHelper()

    @Test
    fun `given a string, when encrypted and then decrypted, it should go back to the original value`() {
        val originalText = "Sample text"

        val encryptedData = sut.encrypt(originalText)
        assertNotNull(encryptedData)
        assertNotEquals(originalText, encryptedData.decodeToString())

        val decryptedText = sut.decrypt(encryptedData)
        assertNotNull(decryptedText)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun `given a string encrypted with another instance, it should be properly decrypted by the SUT`() {
        val otherInstance = DefaultEncryptionHelper()
        val originalText = "Sample text"
        val encryptedData = otherInstance.encrypt(originalText)
        assertNotNull(encryptedData)

        val decryptedText = sut.decrypt(encryptedData)

        assertNotNull(decryptedText)
        assertEquals(originalText, decryptedText)
    }

    @Test
    fun `given a byte array, when encoded and decoded, it should go back to the original values`() {
        val originalBytes = "Sample text".toByteArray()

        val encodedString = sut.encodeToString(originalBytes)
        val decodedBytes = sut.decodeFromString(encodedString)

        assertEquals(originalBytes.toList(), decodedBytes.toList())
    }
}
