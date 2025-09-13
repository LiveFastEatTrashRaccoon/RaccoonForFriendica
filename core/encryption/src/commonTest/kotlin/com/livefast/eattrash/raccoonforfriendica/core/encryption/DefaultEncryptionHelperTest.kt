package com.livefast.eattrash.raccoonforfriendica.core.encryption

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultEncryptionHelperTest {
    private val storage = mock<SecureKeyStorage>(mode = MockMode.autoUnit) {
        every { getKey(any()) } returns null
    }
    private val sut = DefaultEncryptionHelper(
        storage = storage,
        dispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `when encrypt, then result is not null`() = runTest {
        val text = "test string"

        val result = sut.encrypt(text)

        assertNotNull(result)
    }

    @Test
    fun `given previously encrypted, when encrypt again, then result is stable`() = runTest {
        val text = "test string"
        val otherInstance = DefaultEncryptionHelper(
            storage = storage,
            dispatcher = UnconfinedTestDispatcher(),
        )
        val previousEncrypted = otherInstance.encrypt(text)
        val previousDecrypted = otherInstance.decrypt(previousEncrypted)

        val encrypted = sut.encrypt(text)
        val result = sut.decrypt(encrypted)

        assertEquals(previousDecrypted, result)
    }

    @Test
    fun `when decrypt, then result is equal to the original plain text`() = runTest {
        val text = "test string"
        val encrypted = sut.encrypt(text)

        val result = sut.decrypt(encrypted)

        assertEquals(text, result)
    }
}
