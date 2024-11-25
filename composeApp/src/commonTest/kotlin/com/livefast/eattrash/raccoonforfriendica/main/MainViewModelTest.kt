package com.livefast.eattrash.raccoonforfriendica.main

import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MainViewModelTest {
    private val unreadCountFlow = MutableStateFlow(0)
    private val inboxManager =
        mock<InboxManager> { every { unreadCount } returns unreadCountFlow }
    private lateinit var sut: MainViewModel

    @BeforeTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setup() {
        Dispatchers.run { setMain(UnconfinedTestDispatcher()) }
        sut = viewModelFactory()
    }

    @Test
    fun `when initialized then state is as expected`() {
        val state = sut.uiState.value
        val sections =
            listOf(
                BottomNavigationSection.Home,
                BottomNavigationSection.Explore,
                BottomNavigationSection.Inbox(unreadItems = 0),
                BottomNavigationSection.Profile,
            )
        assertEquals(sections, state.bottomNavigationSections)
        assertEquals(0f, state.bottomBarOffsetHeightPx)
    }

    @Test
    fun `when unread count changes then state is as expected`() {
        val count = 1
        unreadCountFlow.update { count }
        val state = sut.uiState.value

        val inboxSection =
            state.bottomNavigationSections.firstNotNullOf {
                it as? BottomNavigationSection.Inbox
            }
        assertEquals(count, inboxSection.unreadItems)
    }

    @Test
    fun `when SetBottomBarOffsetHeightPx then state is as expected`() {
        val bottomBarOffsetHeightPx = 2f
        sut.reduce(MainMviModel.Intent.SetBottomBarOffsetHeightPx(bottomBarOffsetHeightPx))

        val state = sut.uiState.value

        assertEquals(bottomBarOffsetHeightPx, state.bottomBarOffsetHeightPx)
    }

    private fun viewModelFactory() =
        MainViewModel(
            inboxManager = inboxManager,
        )
}
