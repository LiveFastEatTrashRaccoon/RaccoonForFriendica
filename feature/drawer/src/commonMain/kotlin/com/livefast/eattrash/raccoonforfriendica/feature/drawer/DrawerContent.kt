package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.AboutDialog
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ChangeInstanceDialog
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getMainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.components.DrawerHeader
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.components.DrawerShortcut
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(modifier: Modifier = Modifier) {
    val model: DrawerMviModel = getViewModel<DrawerViewModel>()
    val uiState by model.uiState.collectAsState()
    val navigationCoordinator = remember { getNavigationCoordinator() }
    val drawerCoordinator = remember { getDrawerCoordinator() }
    val mainRouter = remember { getMainRouter() }
    val coreResources = remember { getCoreResources() }
    val scope = rememberCoroutineScope()
    var manageAccountsDialogOpened by remember { mutableStateOf(false) }
    var changeInstanceDialogOpened by remember { mutableStateOf(false) }
    var aboutDialogOpened by remember { mutableStateOf(false) }
    val successMessage = LocalStrings.current.messageSuccess

    fun handleAction(action: suspend () -> Unit) {
        scope.launch {
            navigationCoordinator.popUntilRoot()
            drawerCoordinator.toggleDrawer()
            delay(DELAY_EVENT)
            action()
        }
    }

    LaunchedEffect(model) {
        model.effects
            .onEach { event ->
                when (event) {
                    DrawerMviModel.Effect.AnonymousChangeNodeSuccess -> {
                        changeInstanceDialogOpened = false
                        drawerCoordinator.closeDrawer()
                        navigationCoordinator.showGlobalMessage(successMessage)
                    }

                    DrawerMviModel.Effect.AccountChangeSuccess -> {
                        drawerCoordinator.closeDrawer()
                        navigationCoordinator.showGlobalMessage(successMessage)
                    }
                }
            }.launchIn(this)
    }

    ModalDrawerSheet(modifier = modifier) {
        DrawerHeader(
            user = uiState.user,
            autoloadImages = uiState.autoloadImages,
            node = uiState.node,
            canSwitchAccount = uiState.availableAccounts.size > 1,
            onOpenChangeInstance = {
                changeInstanceDialogOpened = true
            },
            onOpenProfile = {
                scope.launch {
                    drawerCoordinator.toggleDrawer()
                    navigationCoordinator.setCurrentSection(BottomNavigationSection.Profile)
                }
            },
            onOpenSwitchAccount = {
                manageAccountsDialogOpened = true
            },
        )

        HorizontalDivider()

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            if (uiState.user == null) {
                Text(
                    modifier = Modifier.padding(horizontal = Spacing.s, vertical = Spacing.s),
                    text = LocalStrings.current.sidebarAnonymousMessage,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                DrawerShortcut(
                    title = LocalStrings.current.favoritesTitle,
                    icon = coreResources.favoriteFill,
                    onSelect = {
                        handleAction { mainRouter.openFavorites() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.bookmarksTitle,
                    icon = coreResources.bookmarksFill,
                    onSelect = {
                        handleAction { mainRouter.openBookmarks() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.followedHashtagsTitle,
                    icon = coreResources.tag,
                    onSelect = {
                        handleAction { mainRouter.openFollowedHashtags() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.followRequestsTitle,
                    icon = coreResources.flaky,
                    onSelect = {
                        handleAction { mainRouter.openFollowRequests() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.manageCirclesTitle,
                    icon = coreResources.workspacesFill,
                    onSelect = {
                        handleAction { mainRouter.openCircles() }
                    },
                )
                if (uiState.hasAnnouncements) {
                    DrawerShortcut(
                        title = LocalStrings.current.announcementsTitle,
                        icon = coreResources.campaign,
                        onSelect = {
                            handleAction { mainRouter.openAnnouncements() }
                        },
                    )
                }
                if (uiState.hasDirectMessages) {
                    DrawerShortcut(
                        title = LocalStrings.current.directMessagesTitle,
                        icon = coreResources.chatFill,
                        onSelect = {
                            handleAction { mainRouter.openDirectMessages() }
                        },
                    )
                }
                if (uiState.hasGallery) {
                    DrawerShortcut(
                        title = LocalStrings.current.galleryTitle,
                        icon = coreResources.dashboard,
                        onSelect = {
                            handleAction { mainRouter.openGallery() }
                        },
                    )
                }
                DrawerShortcut(
                    title = LocalStrings.current.unpublishedTitle,
                    icon = coreResources.stylusFountainPenFill,
                    onSelect = {
                        handleAction { mainRouter.openUnpublished() }
                    },
                )
                if (uiState.hasCalendar) {
                    DrawerShortcut(
                        title = LocalStrings.current.calendarTitle,
                        icon = coreResources.calendarMonthFill,
                        onSelect = {
                            handleAction { mainRouter.openCalendar() }
                        },
                    )
                }
                DrawerShortcut(
                    title = LocalStrings.current.shortcutsTitle,
                    icon = coreResources.exploreFill,
                    onSelect = {
                        handleAction { mainRouter.openShortcuts() }
                    },
                )
            }

            DrawerShortcut(
                title = LocalStrings.current.nodeInfoTitle,
                icon = coreResources.dnsFill,
                onSelect = {
                    handleAction { mainRouter.openNodeInfo() }
                },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.xs))

            DrawerShortcut(
                title = LocalStrings.current.settingsAbout,
                icon = coreResources.support,
                onSelect = {
                    scope.launch {
                        drawerCoordinator.closeDrawer()
                    }
                    aboutDialogOpened = true
                },
            )

            DrawerShortcut(
                title = LocalStrings.current.settingsTitle,
                icon = coreResources.settingsFill,
                onSelect = {
                    handleAction { mainRouter.openSettings() }
                },
            )
        }

        if (manageAccountsDialogOpened) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val items =
                uiState.availableAccounts.map { account ->
                    CustomModalBottomSheetItem(
                        label = account.displayName.orEmpty(),
                        subtitle = account.handle,
                        leadingContent = {
                            val avatar = account.avatar.orEmpty()
                            val avatarSize = IconSize.xl
                            if (avatar.isNotEmpty() && uiState.autoloadImages) {
                                CustomImage(
                                    modifier =
                                    Modifier
                                        .size(avatarSize)
                                        .clip(RoundedCornerShape(avatarSize / 2)),
                                    url = avatar,
                                    quality = FilterQuality.Low,
                                    contentScale = ContentScale.FillBounds,
                                )
                            } else {
                                PlaceholderImage(
                                    size = avatarSize,
                                    title = account.displayName ?: account.handle,
                                )
                            }
                        },
                        trailingContent = {
                            RadioButton(
                                selected = account.active,
                                onClick = {},
                            )
                        },
                    )
                }
            CustomModalBottomSheet(
                title = LocalStrings.current.actionSwitchAccount,
                sheetState = sheetState,
                items = items,
                onSelect = { index ->
                    manageAccountsDialogOpened = false
                    if (index != null) {
                        val accounts = uiState.availableAccounts
                        if (index in accounts.indices) {
                            val selectedAccount = accounts[index]
                            model.reduce(DrawerMviModel.Intent.SwitchAccount(selectedAccount))
                        }
                    }
                },
            )
        }

        if (changeInstanceDialogOpened) {
            ChangeInstanceDialog(
                nodeName = uiState.anonymousChangeNodeName,
                validationInProgress = uiState.anonymousChangeNodeValidationInProgress,
                validationError = uiState.anonymousChangeNodeNameError,
                onClose = {
                    changeInstanceDialogOpened = false
                },
                onNodeChange = { value ->
                    model.reduce(DrawerMviModel.Intent.SetAnonymousChangeNode(value))
                },
                onSubmit = {
                    model.reduce(DrawerMviModel.Intent.SubmitAnonymousChangeNode)
                },
            )
        }

        if (aboutDialogOpened) {
            AboutDialog(
                onClose = {
                    aboutDialogOpened = false
                },
            )
        }
    }
}

private val DELAY_EVENT = 300.milliseconds
