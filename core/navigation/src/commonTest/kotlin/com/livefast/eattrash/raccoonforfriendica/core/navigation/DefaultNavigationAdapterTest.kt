package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultNavigationAdapterTest {

    @Test
    fun `when backStack has one element then canPop is false`() {
        val backStack = NavBackStack<NavKey>(Destination.Main)
        val adapter = DefaultNavigationAdapter(backStack)

        assertFalse(adapter.canPop.value)
    }

    @Test
    fun `when navigating to destination then element is added and canPop is true`() {
        val backStack = NavBackStack<NavKey>(Destination.Main)
        val adapter = DefaultNavigationAdapter(backStack)

        adapter.navigate(Destination.Settings)

        assertEquals(2, backStack.size)
        assertEquals(Destination.Settings, backStack.last())
        assertTrue(adapter.canPop.value)
    }

    @Test
    fun `when navigating with replaceTop then top element is replaced`() {
        val backStack = NavBackStack<NavKey>(Destination.Main, Destination.Settings)
        val adapter = DefaultNavigationAdapter(backStack)

        adapter.navigate(Destination.HashTag("tag"), replaceTop = true)

        assertEquals(2, backStack.size)
        assertEquals(Destination.HashTag("tag"), backStack.last())
        assertTrue(adapter.canPop.value)
    }

    @Test
    fun `when pop called with multiple elements then top element is removed`() {
        val backStack = NavBackStack<NavKey>(Destination.Main, Destination.Settings)
        val adapter = DefaultNavigationAdapter(backStack)

        adapter.pop()

        assertEquals(1, backStack.size)
        assertEquals(Destination.Main, backStack.last())
        assertFalse(adapter.canPop.value)
    }

    @Test
    fun `when pop called with single element then nothing is removed`() {
        val backStack = NavBackStack<NavKey>(Destination.Main)
        val adapter = DefaultNavigationAdapter(backStack)

        adapter.pop()

        assertEquals(1, backStack.size)
        assertEquals(Destination.Main, backStack.first())
        assertFalse(adapter.canPop.value)
    }

    @Test
    fun `when popUntilRoot called then only root remains`() {
        val backStack = NavBackStack<NavKey>(Destination.Main, Destination.Settings, Destination.HashTag("test"))
        val adapter = DefaultNavigationAdapter(backStack)

        adapter.popUntilRoot()

        assertEquals(1, backStack.size)
        assertEquals(Destination.Main, backStack.first())
        assertFalse(adapter.canPop.value)
    }
}
