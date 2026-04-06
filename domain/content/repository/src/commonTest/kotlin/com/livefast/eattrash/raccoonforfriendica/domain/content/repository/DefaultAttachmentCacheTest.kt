package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull

class DefaultAttachmentCacheTest {
    private val sut = DefaultAttachmentCache()

    @Test
    fun `when put then get returns value`() {
        val bytes = byteArrayOf(1, 2, 3)
        sut.put(bytes)
        assertContentEquals(bytes, sut.get())
    }

    @Test
    fun `when clear then get returns null`() {
        val bytes = byteArrayOf(1, 2, 3)
        sut.put(bytes)
        sut.clear()
        assertNull(sut.get())
    }
}
