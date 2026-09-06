package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.DummyUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.ProvideAppCompositionLocals
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.UiDeps
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RuleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NodeInfoScreenScaffoldTest {
    private val uriHandler = mock<UriHandler>()

    private val navigationCoordinator =
        mock<NavigationCoordinator> {
            every { canPop } returns MutableStateFlow(true)
        }
    private val mainRouter = mock<MainRouter>(MockMode.autoUnit)
    private val uiDeps: UiDeps =
        object : DummyUiDeps() {
            override val mainRouter = this@NodeInfoScreenScaffoldTest.mainRouter
            override val navigationCoordinator = this@NodeInfoScreenScaffoldTest.navigationCoordinator
        }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `given all data present when displayed then content is as expected`() {
        with(composeTestRule) {
            setup(
                NodeInfoMviModel.State(
                    info =
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
                    ),
                ),
            )

            onNodeWithText("Node info").assertIsDisplayed()
            onNodeWithText("General").assertIsDisplayed()
            onNodeWithText("Instance title").assertIsDisplayed()
            onNodeWithText("Instance description").assertIsDisplayed()
            onNodeWithText("Contact").assertIsDisplayed()
            onNodeWithText("Admin").assertIsDisplayed()
            onNodeWithText("Rules").assertIsDisplayed()
            onNodeWithTag(NodeInfoTestTags.COLUMN).performScrollToNode(hasText("1.0.0"))
            onNodeWithText("First rule").assertIsDisplayed()
            onNodeWithText("Second rule").assertIsDisplayed()
            onNodeWithText("Other").assertIsDisplayed()
            onNodeWithText("Version").assertIsDisplayed()
            onNodeWithText("1.0.0").assertIsDisplayed()
        }
    }

    @Test
    fun `given no contact when displayed then content is as expected`() {
        with(composeTestRule) {
            setup(
                NodeInfoMviModel.State(
                    info =
                    NodeInfoModel(
                        title = "Instance title",
                        description = "Instance description",
                        rules =
                        listOf(
                            RuleModel(id = "1", text = "First rule"),
                            RuleModel(id = "2", text = "Second rule"),
                        ),
                        version = "1.0.0",
                    ),
                ),
            )

            onNodeWithText("Node info").assertIsDisplayed()
            onNodeWithText("General").assertIsDisplayed()
            onNodeWithText("Instance title").assertIsDisplayed()
            onNodeWithText("Instance description").assertIsDisplayed()
            onNodeWithText("Contact").assertDoesNotExist()
            onNodeWithText("Rules").assertIsDisplayed()
            onNodeWithTag(NodeInfoTestTags.COLUMN).performScrollToNode(hasText("1.0.0"))
            onNodeWithText("First rule").assertIsDisplayed()
            onNodeWithText("Second rule").assertIsDisplayed()
            onNodeWithText("Other").assertIsDisplayed()
            onNodeWithText("Version").assertIsDisplayed()
            onNodeWithText("1.0.0").assertIsDisplayed()
        }
    }

    @Test
    fun `given no rules when displayed then content is as expected`() {
        with(composeTestRule) {
            setup(
                NodeInfoMviModel.State(
                    info =
                    NodeInfoModel(
                        title = "Instance title",
                        description = "Instance description",
                        contact = UserModel(id = "1", displayName = "Admin"),
                        version = "1.0.0",
                    ),
                ),
            )

            onNodeWithText("Node info").assertIsDisplayed()
            onNodeWithText("General").assertIsDisplayed()
            onNodeWithText("Instance title").assertIsDisplayed()
            onNodeWithText("Instance description").assertIsDisplayed()
            onNodeWithText("Contact").assertIsDisplayed()
            onNodeWithText("Admin").assertIsDisplayed()
            onNodeWithText("Rules").assertDoesNotExist()
            onNodeWithTag(NodeInfoTestTags.COLUMN).performScrollToNode(hasText("1.0.0"))
            onNodeWithText("Other").assertIsDisplayed()
            onNodeWithText("Version").assertIsDisplayed()
            onNodeWithText("1.0.0").assertIsDisplayed()
        }
    }

    private fun ComposeContentTestRule.setup(state: NodeInfoMviModel.State) {
        setContent {
            ProvideAppCompositionLocals(
                uiDeps = uiDeps,
                uriHandler = uriHandler,
            ) {
                NodeInfoScreenScaffold(state)
            }
        }
    }
}
