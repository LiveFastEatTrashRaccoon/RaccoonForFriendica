package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.livefast.eattrash.raccoonforfriendica.core.di.testutils.KoinTestRule
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.l10nModule
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.resourcesModule
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
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NodeInfoScreenScaffoldTest {
    private val uriHandler = mock<UriHandler>()

    private val navigationCoordinator =
        mock<NavigationCoordinator> {
            every { canPop } returns MutableStateFlow(true)
        }
    private val mainRouter = mock<MainRouter>(MockMode.autoUnit)

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val diRule =
        KoinTestRule(
            listOf(
                l10nModule,
                resourcesModule,
                module {
                    factory { navigationCoordinator }
                    factory { mainRouter }
                },
            ),
        )

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
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                NodeInfoScreenScaffold(state)
            }
        }
    }
}
