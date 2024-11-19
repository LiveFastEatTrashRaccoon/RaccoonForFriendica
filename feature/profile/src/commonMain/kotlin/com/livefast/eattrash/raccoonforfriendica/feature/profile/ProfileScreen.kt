package com.livefast.eattrash.raccoonforfriendica.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.toWindowInsets
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheet
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomModalBottomSheetItem
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.ProgressHud
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.CustomConfirmDialog
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.loginintro.LoginIntroScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountScreen
import kotlinx.coroutines.launch

class ProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ProfileMviModel>()
        val uiState by model.uiState.collectAsState()
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val scope = rememberCoroutineScope()
        val drawerCoordinator = remember { getDrawerCoordinator() }
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
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    TopAppBar(
                        windowInsets = topAppBarState.toWindowInsets(),
                        scrollBehavior = scrollBehavior,
                        title = {
                            Text(
                                text = LocalStrings.current.sectionTitleProfile,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerCoordinator.toggleDrawer()
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    manageAccountsDialogOpened = true
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ManageAccounts,
                                    contentDescription = null,
                                )
                            }
                            if (uiState.currentUserId != null) {
                                IconButton(
                                    onClick = {
                                        confirmLogoutDialogOpened = true
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.Logout,
                                        contentDescription = null,
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
                        val screen =
                            if (uiState.currentUserId != null) {
                                MyAccountScreen
                            } else {
                                LoginIntroScreen()
                            }
                        screen.Content()
                    }
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
                } +
                    CustomModalBottomSheetItem(
                        label = LocalStrings.current.actionAddNew,
                        leadingContent = {
                            IconButton(
                                onClick = {},
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                )
                            }
                        },
                    )
            CustomModalBottomSheet(
                title = LocalStrings.current.actionSwitchAccount,
                sheetState = sheetState,
                items = items,
                onSelected = { index ->
                    manageAccountsDialogOpened = false
                    if (index != null) {
                        val accounts = uiState.availableAccounts
                        if (index in accounts.indices) {
                            val selectedAccount = accounts[index]
                            model.reduce(ProfileMviModel.Intent.SwitchAccount(selectedAccount))
                        } else {
                            model.reduce(ProfileMviModel.Intent.AddAccount)
                        }
                    }
                },
                onLongPress = { index ->
                    manageAccountsDialogOpened = false
                    val selectedAccount = uiState.availableAccounts[index]
                    if (!selectedAccount.active) {
                        confirmDeleteAccount = selectedAccount
                    }
                },
            )
        }

        if (confirmDeleteAccount != null) {
            CustomConfirmDialog(
                title = LocalStrings.current.actionDeleteAccount,
                onClose = { confirm ->
                    val account = confirmDeleteAccount
                    confirmDeleteAccount = null
                    if (confirm && account != null) {
                        model.reduce(ProfileMviModel.Intent.DeleteAccount(account))
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
}
