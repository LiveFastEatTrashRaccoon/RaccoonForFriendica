package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.getViewModel
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.resources.CoreResources
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt

@Composable
fun PermanentDrawerContent(
    currentDestination: Destination,
    onSelectDestination: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val model: PermanentDrawerMviModel = getViewModel<PermanentDrawerViewModel>()
    val uiState by model.uiState.collectAsState()
    val destinations = buildList {
        add(Destination.Main)
        add(Destination.Explore)
        if (uiState.isLogged) {
            add(Destination.Inbox)
            add(Destination.Profile)
            add(Destination.Favorites(type = FavoritesType.Favorites.toInt()))
            add(Destination.Favorites(type = FavoritesType.Bookmarks.toInt()))
            add(Destination.FollowedHashtags)
            add(Destination.FollowRequests)
            add(Destination.Circles)
            if (uiState.hasAnnouncements) {
                add(Destination.Announcements)
            }
            if (uiState.hasDirectMessages) {
                add(Destination.ConversationList)
            }
            if (uiState.hasGallery) {
                add(Destination.Gallery)
            }
            add(Destination.Unpublished)
            if (uiState.hasCalendar) {
                add(Destination.Calendar)
            }
            add(Destination.ShortcutList)
        }
        add(Destination.NodeInfo)
        add(Destination.Settings)
    }
    val coreResources = getCoreResources()

    val drawerWidth by animateDpAsState(if (uiState.isExpanded) 220.dp else 72.dp)

    Column(
        modifier = modifier
            .padding(Spacing.xs)
            .width(drawerWidth)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.Start)
                .padding(
                    start = if (uiState.isExpanded) {
                        Spacing.xs
                    } else {
                        Spacing.s
                    },
                ),
            onClick = {
                model.reduce(PermanentDrawerMviModel.Intent.ToggleExpanded)
            },
        ) {
            Icon(
                imageVector = coreResources.menu,
                contentDescription = null,
            )
        }

        for (destination in destinations) {
            NavigationDrawerItem(
                modifier = Modifier
                    .then(
                        if (uiState.isExpanded) Modifier.fillMaxWidth() else Modifier,
                    ),
                selected = currentDestination == destination,
                label = {
                    if (uiState.isExpanded) {
                        Text(
                            text = destination.toReadableName(),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier
                            .then(
                                if (uiState.isExpanded) Modifier else Modifier.padding(start = Spacing.s),
                            ),
                        imageVector = destination.toIcon(coreResources),
                        contentDescription = null,
                    )
                },
                badge = {
                    if (destination == Destination.Inbox && uiState.unreadItems > 0 && uiState.isExpanded) {
                        Badge {
                            Text(
                                text = if (uiState.unreadItems <= 10) "$uiState.unreadItems" else "10+",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                            )
                        }
                    }
                },
                onClick = {
                    onSelectDestination(destination)
                },
            )
        }
    }
}

@Composable
private fun Destination.toIcon(coreResources: CoreResources): ImageVector = when (this) {
    Destination.Main -> coreResources.homeFill
    Destination.Explore -> coreResources.exploreFill
    Destination.Inbox -> coreResources.inboxFill
    is Destination.Favorites -> when (type) {
        FavoritesType.Favorites.toInt() -> coreResources.favoriteFill
        else -> coreResources.bookmarksFill
    }

    Destination.FollowedHashtags -> coreResources.tag
    Destination.FollowRequests -> coreResources.flaky
    Destination.Circles -> coreResources.workspacesFill
    Destination.Announcements -> coreResources.campaign
    Destination.ConversationList -> coreResources.chatFill
    Destination.Gallery -> coreResources.dashboard
    Destination.Unpublished -> coreResources.stylusFountainPenFill
    Destination.Calendar -> coreResources.calendarMonthFill
    Destination.ShortcutList -> coreResources.exploreFill
    Destination.Profile -> coreResources.accountCircleFill
    Destination.NodeInfo -> coreResources.dnsFill
    Destination.Settings -> coreResources.settingsFill
    else -> coreResources.homeFill
}

@Composable
private fun Destination.toReadableName(): String = when (this) {
    Destination.Main -> LocalStrings.current.sectionTitleHome
    Destination.Explore -> LocalStrings.current.sectionTitleExplore
    Destination.Inbox -> LocalStrings.current.sectionTitleInbox
    is Destination.Favorites -> when (type) {
        FavoritesType.Favorites.toInt() -> LocalStrings.current.favoritesTitle
        else -> LocalStrings.current.bookmarksTitle
    }

    Destination.FollowedHashtags -> LocalStrings.current.followedHashtagsTitle
    Destination.FollowRequests -> LocalStrings.current.followRequestsTitle
    Destination.Circles -> LocalStrings.current.manageCirclesTitle
    Destination.Announcements -> LocalStrings.current.announcementsTitle
    Destination.ConversationList -> LocalStrings.current.directMessagesTitle
    Destination.Gallery -> LocalStrings.current.galleryTitle
    Destination.Unpublished -> LocalStrings.current.unpublishedTitle
    Destination.Calendar -> LocalStrings.current.calendarTitle
    Destination.ShortcutList -> LocalStrings.current.shortcutsTitle
    Destination.Profile -> LocalStrings.current.sectionTitleProfile
    Destination.NodeInfo -> LocalStrings.current.nodeInfoTitle
    Destination.Settings -> LocalStrings.current.settingsTitle
    else -> ""
}
