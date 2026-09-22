package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.di.utils.LocalUiDeps
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.isWidthSizeClassBelow
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.optimizedForLargeScreens
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.delete.DeleteAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.delete.DeleteAccountViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.switchaccount.SwitchAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.switchaccount.SwitchAccountViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    model: ProfileMviModel,
    myAccountModel: MyAccountMviModel,
    modifier: Modifier = Modifier,
    myAccountLazyListState: LazyListState = rememberLazyListState(),
) {
    val uiState by model.uiState.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val scope = rememberCoroutineScope()
    val drawerCoordinator = LocalUiDeps.current.drawerCoordinator
    val navigationCoordinator = LocalUiDeps.current.navigationCoordinator
    val successMessage = LocalStrings.current.messageSuccess
    var confirmLogoutDialogOpened by remember { mutableStateOf(false) }
    var manageAccountsDialogOpened by remember { mutableStateOf(false) }
    var confirmDeleteAccount by remember { mutableStateOf<AccountModel?>(null) }

    CompositionLocalProvider(
        LocalProfileTopAppBarStateWrapper provides
            object : ProfileTopAppBarStateWrapper {
                override val topAppBarState: TopAppBarState
                    get() = topAppBarState
            },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    windowInsets = topAppBarState.toWindowInsets().optimizedForLargeScreens(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = LocalStrings.current.sectionTitleProfile,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (isWidthSizeClassBelow(WindowWidthSizeClass.Expanded)) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerCoordinator.toggleDrawer()
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = LocalResources.current.menu,
                                    contentDescription = LocalStrings.current.actionOpenSideMenu,
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                manageAccountsDialogOpened = true
                            },
                        ) {
                            Icon(
                                imageVector = LocalResources.current.changeCircle,
                                contentDescription = LocalStrings.current.actionSwitchAccount,
                            )
                        }
                        if (uiState.currentUserId != null) {
                            IconButton(
                                onClick = {
                                    confirmLogoutDialogOpened = true
                                },
                            ) {
                                Icon(
                                    imageVector = LocalResources.current.logout,
                                    contentDescription = LocalStrings.current.actionLogout,
                                )
                            }
                        }
                    },
                )
            },
            content = { padding ->
                Box(
                    modifier =
                        Modifier
                            .padding(padding)
                            .then(
                                if (uiState.hideNavigationBarWhileScrolling) {
                                    Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                                } else {
                                    Modifier
                                },
                            ),
                ) {
                    if (uiState.currentUserId != null) {
                        MyAccountScreen(
                            model = myAccountModel,
                            lazyListState = myAccountLazyListState,
                        )
                    } else {
                        LoginIntroScreen()
                    }
                }
            },
        )
    }

    if (manageAccountsDialogOpened) {
        val viewModelStoreOwner = rememberViewModelStoreOwner()
        val  switchAccountModel: SwitchAccountMviModel = metroViewModel<SwitchAccountViewModel>(viewModelStoreOwner)
        val dialogUiState by switchAccountModel.uiState.collectAsState()

        LaunchedEffect(switchAccountModel) {
            switchAccountModel.effects.onEach { effect ->
                when (effect) {
                    SwitchAccountMviModel.Effect.AccountChangeSuccess -> {
                        navigationCoordinator.showGlobalMessage(successMessage)
                    }
                }
            }.launchIn(this)
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val items =
            dialogUiState.availableAccounts.map { account ->
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
            } +
                CustomModalBottomSheetItem(
                    label = LocalStrings.current.actionAddNew,
                    leadingContent = {
                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                imageVector = LocalResources.current.addCircle,
                                contentDescription = LocalStrings.current.actionAddNew,
                            )
                        }
                    },
                )
        CustomModalBottomSheet(
            title = LocalStrings.current.actionSwitchAccount,
            sheetState = sheetState,
            items = items,
            onSelect = { index ->
                manageAccountsDialogOpened = false
                if (index != null) {
                    val accounts = dialogUiState.availableAccounts
                    if (index in accounts.indices) {
                        val selectedAccount = accounts[index]
                        switchAccountModel.reduce(SwitchAccountMviModel.Intent.SwitchAccount(selectedAccount))
                    } else {
                        switchAccountModel.reduce(SwitchAccountMviModel.Intent.AddAccount)
                    }
                }
            },
            onLongPress = { index ->
                manageAccountsDialogOpened = false
                val selectedAccount = dialogUiState.availableAccounts[index]
                if (!selectedAccount.active) {
                    confirmDeleteAccount = selectedAccount
                }
            },
        )
    }

    if (confirmDeleteAccount != null) {
        val viewModelStoreOwner = rememberViewModelStoreOwner()
        val deleteAccountModel: DeleteAccountMviModel = metroViewModel<DeleteAccountViewModel>(viewModelStoreOwner)
        LaunchedEffect(deleteAccountModel) {
            deleteAccountModel.effects.onEach { effect ->
                when (effect) {
                    DeleteAccountMviModel.Effect.Success -> {
                        confirmDeleteAccount = null
                    }
                }
            }.launchIn(this)
        }
        CustomConfirmDialog(
            title = LocalStrings.current.actionDeleteAccount,
            onClose = { confirm ->
                val account = confirmDeleteAccount
                if (confirm && account != null) {
                    deleteAccountModel.reduce(DeleteAccountMviModel.Intent.Submit(account))
                }
            },
        )
    }

    if (confirmLogoutDialogOpened) {
        CustomConfirmDialog(
            title = LocalStrings.current.actionLogout,
            onClose = { confirm ->
                confirmLogoutDialogOpened = false
                if (confirm) {
                    model.reduce(ProfileMviModel.Intent.Logout)
                }
            },
        )
    }

    if (uiState.loading) {
        ProgressHud()
    }
}
