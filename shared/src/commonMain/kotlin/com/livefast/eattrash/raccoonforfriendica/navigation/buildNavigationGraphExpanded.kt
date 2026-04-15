package com.livefast.eattrash.raccoonforfriendica.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.adaptive.CircleTimelineWithEntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.adaptive.CirclesWithCircleMembersScreen
import com.livefast.eattrash.raccoonforfriendica.adaptive.EntryListWithEntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.adaptive.HashtagWithEntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.adaptive.SearchWithEntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.adaptive.UserDetailWithEntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.WebViewScreen
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main.AcknowledgementsScreen
import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.manage.ManageUserCirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrylist.EntryListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksScreen
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.newaccount.NewAccountScreen
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadScreen
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen

internal fun NavGraphBuilder.buildNavigationGraphExpanded(
    timelineViewModel: TimelineMviModel,
    timelineLazyListState: LazyListState,
    exploreViewModel: ExploreMviModel,
    exploreLazyListState: LazyListState,
    inboxViewModel: InboxMviModel,
    inboxLazyListState: LazyListState,
    profileViewModel: ProfileMviModel,
    myAccountViewModel: MyAccountMviModel,
    myAccountLazyListState: LazyListState,
    favoritesViewModel: EntryListMviModel,
    bookmarksViewModel: EntryListMviModel,
    followedHashtagsViewModel: FollowedHashtagsMviModel,
    followRequestsViewModel: FollowRequestsMviModel,
    circlesViewModel: CirclesMviModel,
    conversationListViewModel: ConversationListMviModel,
    galleryViewModel: GalleryMviModel,
    unpublishedViewModel: UnpublishedMviModel,
    calendarViewModel: CalendarMviModel,
    shortcutListViewModel: ShortcutListMviModel,
    nodeInfoViewModel: NodeInfoMviModel,
) {
    composable<Destination.Main> {
        MainScreen(
            timelineViewModel = timelineViewModel,
            exploreViewModel = exploreViewModel,
            inboxViewModel = inboxViewModel,
            profileViewModel = profileViewModel,
            myAccountViewModel = myAccountViewModel,
            timelineLazyListState = timelineLazyListState,
            exploreLazyListState = exploreLazyListState,
            inboxLazyListState = inboxLazyListState,
            myAccountLazyListState = myAccountLazyListState,
            lockedSection = BottomNavigationSection.Home,
        )
    }
    composable<Destination.EntryDetail> {
        val route: Destination.EntryDetail = it.toRoute()
        EntryDetailScreen(
            id = route.entryId,
            swipeNavigationEnabled = route.swipeNavigationEnabled,
            otherInstance = route.otherInstance,
        )
    }
    composable<Destination.ForumList> {
        val route: Destination.ForumList = it.toRoute()
        ForumListScreen(id = route.userId, otherInstance = route.otherInstance)
    }
    composable<Destination.UserDetail> {
        val route: Destination.UserDetail = it.toRoute()
        UserDetailWithEntryDetailScreen(id = route.userId, otherInstance = route.otherInstance)
    }
    composable<Destination.Settings> {
        SettingsScreen()
    }
    composable<Destination.HashTag> {
        val route: Destination.HashTag = it.toRoute()
        HashtagWithEntryDetailScreen(tag = route.tag, otherInstance = route.otherInstance)
    }
    composable<Destination.UserList> {
        val route: Destination.UserList = it.toRoute()
        UserListScreen(
            type = route.type,
            userId = route.userId,
            entryId = route.entryId,
            infoCount = route.infoCount,
            enableExport = route.enableExport,
            otherInstance = route.otherInstance,
        )
    }
    composable<Destination.Favorites> {
        EntryListWithEntryDetailScreen(model = favoritesViewModel, listTitle = LocalStrings.current.favoritesTitle)
    }
    composable<Destination.Bookmarks> {
        EntryListWithEntryDetailScreen(model = bookmarksViewModel, listTitle = LocalStrings.current.bookmarksTitle)
    }
    composable<Destination.FollowedHashtags> {
        FollowedHashtagsScreen(followedHashtagsViewModel)
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
        SearchWithEntryDetailScreen()
    }
    composable<Destination.Thread> {
        val route: Destination.Thread = it.toRoute()
        ThreadScreen(
            entryId = route.entryId,
            swipeNavigationEnabled = route.swipeNavigationEnabled,
            otherInstance = route.otherInstance,
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
        CirclesWithCircleMembersScreen(circlesViewModel)
    }
    composable<Destination.CircleMembers> {
        val route: Destination.CircleMembers = it.toRoute()
        CircleMembersScreen(route.circleId)
    }
    composable<Destination.CircleTimeline> {
        val route: Destination.CircleTimeline = it.toRoute()
        CircleTimelineWithEntryDetailScreen(route.circleId)
    }
    composable<Destination.FollowRequests> {
        FollowRequestsScreen(followRequestsViewModel)
    }
    composable<Destination.EditProfile> {
        EditProfileScreen()
    }
    composable<Destination.NodeInfo> {
        NodeInfoScreen(nodeInfoViewModel)
    }
    composable<Destination.ConversationList> {
        ConversationListScreen(conversationListViewModel)
    }
    composable<Destination.Conversation> {
        val route: Destination.Conversation = it.toRoute()
        ConversationScreen(
            otherUserId = route.otherUserId,
            parentUri = route.parentUri,
        )
    }
    composable<Destination.Gallery> {
        GalleryScreen(galleryViewModel)
    }
    composable<Destination.AlbumDetail> {
        val route: Destination.AlbumDetail = it.toRoute()
        AlbumDetailScreen(route.name)
    }
    composable<Destination.Unpublished> {
        UnpublishedScreen(unpublishedViewModel)
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
        CalendarScreen(calendarViewModel)
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
        ShortcutListScreen(shortcutListViewModel)
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
    composable<Destination.ManageUserCircles> {
        val route: Destination.ManageUserCircles = it.toRoute()
        ManageUserCirclesScreen(userId = route.userId)
    }
    composable<Destination.Explore> {
        MainScreen(
            timelineViewModel = timelineViewModel,
            exploreViewModel = exploreViewModel,
            inboxViewModel = inboxViewModel,
            profileViewModel = profileViewModel,
            myAccountViewModel = myAccountViewModel,
            timelineLazyListState = timelineLazyListState,
            exploreLazyListState = exploreLazyListState,
            inboxLazyListState = inboxLazyListState,
            myAccountLazyListState = myAccountLazyListState,
            lockedSection = BottomNavigationSection.Explore,
        )
    }
    composable<Destination.Inbox> {
        MainScreen(
            timelineViewModel = timelineViewModel,
            exploreViewModel = exploreViewModel,
            inboxViewModel = inboxViewModel,
            profileViewModel = profileViewModel,
            myAccountViewModel = myAccountViewModel,
            timelineLazyListState = timelineLazyListState,
            exploreLazyListState = exploreLazyListState,
            inboxLazyListState = inboxLazyListState,
            myAccountLazyListState = myAccountLazyListState,
            lockedSection = BottomNavigationSection.Inbox(0),
        )
    }
    composable<Destination.Profile> {
        MainScreen(
            timelineViewModel = timelineViewModel,
            exploreViewModel = exploreViewModel,
            inboxViewModel = inboxViewModel,
            profileViewModel = profileViewModel,
            myAccountViewModel = myAccountViewModel,
            timelineLazyListState = timelineLazyListState,
            exploreLazyListState = exploreLazyListState,
            inboxLazyListState = inboxLazyListState,
            myAccountLazyListState = myAccountLazyListState,
            lockedSection = BottomNavigationSection.Profile,
        )
    }
}
