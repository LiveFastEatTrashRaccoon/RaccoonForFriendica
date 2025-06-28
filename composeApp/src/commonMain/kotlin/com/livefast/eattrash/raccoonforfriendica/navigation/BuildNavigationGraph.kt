package com.livefast.eattrash.raccoonforfriendica.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.WebViewScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main.AcknowledgementsScreen
import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagScreen
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksScreen
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.newaccount.NewAccountScreen
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadScreen
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchScreen
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen

internal fun NavGraphBuilder.buildNavigationGraph() {
    composable<Destination.Main> {
        MainScreen()
    }
    composable<Destination.EntryDetail> {
        val route: Destination.EntryDetail = it.toRoute()
        EntryDetailScreen(
            id = route.entryId,
            swipeNavigationEnabled = route.swipeNavigationEnabled,
        )
    }
    composable<Destination.ForumList> {
        val route: Destination.ForumList = it.toRoute()
        ForumListScreen(id = route.userId)
    }
    composable<Destination.UserDetail> {
        val route: Destination.UserDetail = it.toRoute()
        UserDetailScreen(id = route.userId)
    }
    composable<Destination.Settings> {
        SettingsScreen()
    }
    composable<Destination.HashTag> {
        val route: Destination.HashTag = it.toRoute()
        HashtagScreen(route.tag)
    }
    composable<Destination.UserList> {
        val route: Destination.UserList = it.toRoute()
        UserListScreen(
            type = route.type,
            userId = route.userId,
            entryId = route.entryId,
            infoCount = route.infoCount,
            enableExport = route.enableExport,
        )
    }
    composable<Destination.Favorites> {
        val route: Destination.Favorites = it.toRoute()
        FavoritesScreen(route.type)
    }
    composable<Destination.FollowedHashtags> {
        FollowedHashtagsScreen()
    }
    composable<Destination.Composer> {
        val route: Destination.Composer = it.toRoute()
        ComposerScreen(
            inReplyToId = route.inReplyToId,
            inReplyToUsername = route.inReplyToUsername,
            inReplyToHandle = route.inReplyToHandle,
            groupUsername = route.groupUsername,
            groupHandle = route.groupHandle,
            editedPostId = route.editedPostId,
            scheduledPostId = route.scheduledPostId,
            draftId = route.draftId,
            urlToShare = route.urlToShare,
            initialText = route.initialText,
        )
    }
    composable<Destination.Search> {
        SearchScreen()
    }
    composable<Destination.Thread> {
        val destination: Destination.Thread = it.toRoute()
        ThreadScreen(
            entryId = destination.entryId,
            swipeNavigationEnabled = destination.swipeNavigationEnabled,
        )
    }
    composable<Destination.ImageDetail> {
        val route: Destination.ImageDetail = it.toRoute()
        ImageDetailScreen(
            urls = route.urls,
            initialIndex = route.initialIndex,
            videoIndices = route.videoIndices,
        )
    }
    composable<Destination.ManageBlocks> {
        ManageBlocksScreen()
    }
    composable<Destination.Circles> {
        CirclesScreen()
    }
    composable<Destination.CircleMembers> {
        val route: Destination.CircleMembers = it.toRoute()
        CircleMembersScreen(route.circleId)
    }
    composable<Destination.CircleTimeline> {
        val route: Destination.CircleTimeline = it.toRoute()
        CircleTimelineScreen(route.circleId)
    }
    composable<Destination.FollowRequests> {
        FollowRequestsScreen()
    }
    composable<Destination.EditProfile> {
        EditProfileScreen()
    }
    composable<Destination.NodeInfo> {
        NodeInfoScreen()
    }
    composable<Destination.ConversationList> {
        ConversationListScreen()
    }
    composable<Destination.Conversation> {
        val route: Destination.Conversation = it.toRoute()
        ConversationScreen(
            otherUserId = route.otherUserId,
            parentUri = route.parentUri,
        )
    }
    composable<Destination.Gallery> {
        GalleryScreen()
    }
    composable<Destination.AlbumDetail> {
        val route: Destination.AlbumDetail = it.toRoute()
        AlbumDetailScreen(route.name)
    }
    composable<Destination.Unpublished> {
        UnpublishedScreen()
    }
    composable<Destination.CreateReport> {
        val route: Destination.CreateReport = it.toRoute()
        CreateReportScreen(
            userId = route.userId,
            entryId = route.entryId,
        )
    }
    composable<Destination.UserFeedback> {
        UserFeedbackScreen()
    }
    composable<Destination.Calendar> {
        CalendarScreen()
    }
    composable<Destination.EventDetail> {
        val route: Destination.EventDetail = it.toRoute()
        EventDetailScreen(route.eventId)
    }
    composable<Destination.Licences> {
        LicencesScreen()
    }
    composable<Destination.WebView> {
        val route: Destination.WebView = it.toRoute()
        WebViewScreen(route.url)
    }
    composable<Destination.Announcements> {
        AnnouncementsScreen()
    }
    composable<Destination.Acknowledgements> {
        AcknowledgementsScreen()
    }
    composable<Destination.ShortcutList> {
        ShortcutListScreen()
    }
    composable<Destination.ShortcutTimeline> {
        val route: Destination.ShortcutTimeline = it.toRoute()
        ShortcutTimelineScreen(route.node)
    }
    composable<Destination.Login> {
        val route: Destination.Login = it.toRoute()
        LoginScreen(route.type)
    }
    composable<Destination.LegacyLogin> {
        LegacyLoginScreen()
    }
    composable<Destination.NewAccount> {
        NewAccountScreen()
    }
}
