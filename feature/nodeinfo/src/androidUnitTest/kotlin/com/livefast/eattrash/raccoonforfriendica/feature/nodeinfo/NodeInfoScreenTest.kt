package com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import cafe.adriel.voyager.navigator.Navigator
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.testutils.MockStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.testutils.KodeinTestApplication
import com.livefast.eattrash.raccoonforfriendica.core.testutils.KodeinTestRule
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
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = KodeinTestApplication::class)
class NodeInfoScreenTest {
    private val uriHandler = mock<UriHandler>()

    private val viewModel =
        mock<NodeInfoMviModel>(MockMode.autoUnit) {
            every { uiState }
        }
    private val navigationCoordinator =
        mock<NavigationCoordinator> {
            every { canPop } returns MutableStateFlow(true)
        }
    private val detailOpener = mock<DetailOpener>(MockMode.autoUnit)

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val diRule =
        KodeinTestRule(
            listOf(
                DI.Module("NodeInfoScreenTestModule") {
                    bind<NodeInfoMviModel> { provider { viewModel } }
                    bind<NavigationCoordinator> { provider { navigationCoordinator } }
                    bind<DetailOpener> { provider { detailOpener } }
                },
            ),
        )

    @Test
    fun `given all data present when displayed then content is as expected`() {
        with(composeTestRule) {
            every {
                viewModel.uiState
            } returns
                MutableStateFlow(
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

            setup()

            onNodeWithText("Node Info").assertIsDisplayed()
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
            every {
                viewModel.uiState
            } returns
                MutableStateFlow(
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

            setup()

            onNodeWithText("Node Info").assertIsDisplayed()
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
            every {
                viewModel.uiState
            } returns
                MutableStateFlow(
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

            setup()

            onNodeWithText("Node Info").assertIsDisplayed()
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

    private fun ComposeContentTestRule.setup() {
        setContent {
            CompositionLocalProvider(
                LocalStrings provides
                    MockStrings().apply {
                        this["nodeInfoTitle"] = "Node Info"
                        this["settingsHeaderGeneral"] = "General"
                        this["nodeInfoSectionContact"] = "Contact"
                        this["nodeInfoSectionRules"] = "Rules"
                        this["itemOther"] = "Other"
                        this["settingsAboutAppVersion"] = "Version"
                        this["actionGoBack"] = "Go back"
                        this["actionOpenDetail"] = "Open detail"
                    },
                LocalUriHandler provides uriHandler,
            ) {
                Navigator(NodeInfoScreen())
            }
        }
    }
}
