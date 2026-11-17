package com.livefast.eattrash.raccoonforfriendica.core.architecture

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListExtensionsTest {

    @Test
    fun `given matching item when updateMatching then item is updated`() {
        val list = listOf("a", "b", "c")

        val result = list.updateMatching(
            matcher = { it == "b" },
            block = { it.uppercase() },
        )

        assertEquals(listOf("a", "B", "c"), result)
    }

    @Test
    fun `given no matching item when updateMatching then list is unchanged`() {
        val list = listOf("a", "b", "c")

        val result = list.updateMatching(
            matcher = { it == "d" },
            block = { it.uppercase() },
        )

        assertEquals(list, result)
    }

    @Test
    fun `given empty list when updateMatching then empty list returned`() {
        val list = emptyList<String>()

        val result = list.updateMatching(
            matcher = { true },
            block = { it.uppercase() },
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun `given matching item when removeMatching then item is removed`() {
        val list = listOf("a", "b", "c")

        val result = list.removeMatching { it == "b" }

        assertEquals(listOf("a", "c"), result)
    }

    @Test
    fun `given no matching item when removeMatching then list is unchanged`() {
        val list = listOf("a", "b", "c")

        val result = list.removeMatching { it == "d" }

        assertEquals(list, result)
    }

    @Test
    fun `given empty list when removeMatching then empty list returned`() {
        val list = emptyList<String>()

        val result = list.removeMatching { true }

        assertTrue(result.isEmpty())
    }
}
