package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.NodeInfoRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NodeInfoViewModelTest {
    private val nodeInfo =
        NodeInfoModel(
            title = "Instance title",
            description = "Instance description",
            contact = UserModel(id = "1", displayName = "Admin"),
            rules =
                listOf(
                    RuleModel(id = "1", text = "First rule"),
                    RuleModel(id = "2", text = "Second rule"),
                ),
            version = "1.0.0",
        )
    private val nodeInfoRepository =
        mock<NodeInfoRepository> {
            everySuspend { getInfo() } returns nodeInfo
        }
    private val imageAutoloadObserver =
        mock<ImageAutoloadObserver> {
            every { enabled } returns MutableStateFlow(true)
        }
    private val emojiHelper =
        mock<EmojiHelper> {
            everySuspend { any<UserModel>().withEmojisIfMissing() } returnsArgAt (0)
        }

    private lateinit var sut: NodeInfoMviModel

    @BeforeTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setup() {
        Dispatchers.run { setMain(UnconfinedTestDispatcher()) }
        sut = viewModelFactory()
    }

    @AfterTest
    @OptIn(ExperimentalCoroutinesApi::class)
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when initialized then state is as expected`() {
        val state = sut.uiState.value
        assertEquals(nodeInfo, state.info)
        assertTrue(state.autoloadImages)

        verifySuspend {
            nodeInfoRepository.getInfo()
        }
    }

    @Test
    fun `given autoload images disabled when initialized then state is as expected`() {
        every { imageAutoloadObserver.enabled } returns MutableStateFlow(false)
        sut = viewModelFactory()

        val state = sut.uiState.value
        assertEquals(nodeInfo, state.info)
        assertFalse(state.autoloadImages)

        verifySuspend {
            nodeInfoRepository.getInfo()
        }
    }

    private fun viewModelFactory() =
        NodeInfoViewModel(
            nodeInfoRepository = nodeInfoRepository,
            emojiHelper = emojiHelper,
            imageAutoloadObserver = imageAutoloadObserver,
        )
}
