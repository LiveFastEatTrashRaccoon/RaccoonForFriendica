package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flaky
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getDrawerCoordinator
import com.livefast.eattrash.raccoonforfriendica.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.components.DrawerHeader
import com.livefast.eattrash.raccoonforfriendica.feature.drawer.components.DrawerShortcut
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DrawerContent : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<DrawerMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val drawerCoordinator = remember { getDrawerCoordinator() }
        val detailOpener = remember { getDetailOpener() }
        val scope = rememberCoroutineScope()

        fun handleOpen(action: suspend () -> Unit) {
            scope.launch {
                navigationCoordinator.popUntilRoot()
                drawerCoordinator.toggleDrawer()
                delay(50)
                action()
            }
        }

        ModalDrawerSheet {
            DrawerHeader(
                user = uiState.user,
                node = uiState.node,
            )

            HorizontalDivider()

            if (uiState.user == null) {
                Text(
                    modifier = Modifier.padding(horizontal = Spacing.s, vertical = Spacing.s),
                    text = LocalStrings.current.sidebarAnonymousMessage,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                DrawerShortcut(
                    title = LocalStrings.current.favoritesTitle,
                    icon = Icons.Default.Favorite,
                    onSelected = {
                        handleOpen { detailOpener.openFavorites() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.bookmarksTitle,
                    icon = Icons.Default.Bookmarks,
                    onSelected = {
                        handleOpen { detailOpener.openBookmarks() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.followedHashtagsTitle,
                    icon = Icons.Default.Tag,
                    onSelected = {
                        handleOpen { detailOpener.openFollowedHashtags() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.followRequestsTitle,
                    icon = Icons.Default.Flaky,
                    onSelected = {
                        handleOpen { detailOpener.openFollowRequests() }
                    },
                )
                DrawerShortcut(
                    title = LocalStrings.current.manageCirclesTitle,
                    icon = Icons.Default.Workspaces,
                    onSelected = {
                        handleOpen { detailOpener.openCircles() }
                    },
                )
            }

            DrawerShortcut(
                title = LocalStrings.current.nodeInfoTitle,
                icon = Icons.Default.Info,
                onSelected = {
                    handleOpen { detailOpener.openNodeInfo() }
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Spacing.xs),
            )

            DrawerShortcut(
                title = LocalStrings.current.settingsTitle,
                icon = Icons.Default.Settings,
                onSelected = {
                    handleOpen { detailOpener.openSettings() }
                },
            )
        }
    }
}
