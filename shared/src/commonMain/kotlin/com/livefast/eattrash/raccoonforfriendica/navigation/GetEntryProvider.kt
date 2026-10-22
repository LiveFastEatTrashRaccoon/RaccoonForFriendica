package com.livefast.eattrash.raccoonforfriendica.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.WebViewScreen
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.navigation.BottomNavigationSection
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostListType
import com.livefast.eattrash.raccoonforfriendica.feature.acknowledgements.main.AcknowledgementsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarMvi
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesMvi
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.circles.manage.ManageUserCirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListMvi
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.explore.ExploreMvi
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsMvi
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryMvi
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsMvi
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagScreen
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.inbox.InboxMvi
import com.livefast.eattrash.raccoonforfriendica.feature.licences.LicencesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.login.legacy.LegacyLoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.login.oauth.LoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksScreen
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoMvi
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoScreen
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.postdetail.PostDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.postlist.PostListMvi
import com.livefast.eattrash.raccoonforfriendica.feature.postlist.PostListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.postlist.PostListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.profile.ProfileMvi
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.myaccount.MyAccountMvi
import com.livefast.eattrash.raccoonforfriendica.feature.profile.newaccount.NewAccountScreen
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListMvi
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadScreen
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineMvi
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedMvi
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedScreen
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchScreen
import com.livefast.eattrash.raccoonforfriendica.main.MainScreen
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel

internal val NavKey.isDetailDestination: Boolean
    get() =
        this is Destination.PostDetail ||
            this is Destination.CircleMembers ||
            this is Destination.Conversation ||
            this is Destination.EventDetail ||
            this is Destination.AlbumDetail

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal fun getEntryProvider(
    timelineViewModel: TimelineMvi,
    timelineLazyListState: LazyListState,
    exploreViewModel: ExploreMvi,
    exploreLazyListState: LazyListState,
    inboxViewModel: InboxMvi,
    inboxLazyListState: LazyListState,
    profileViewModel: ProfileMvi,
    myAccountViewModel: MyAccountMvi,
    myAccountLazyListState: LazyListState,
    favoritesViewModel: PostListMvi? = null,
    bookmarksViewModel: PostListMvi? = null,
    followedHashtagsViewModel: FollowedHashtagsMvi? = null,
    followRequestsViewModel: FollowRequestsMvi? = null,
    circlesViewModel: CirclesMvi? = null,
    conversationListViewModel: ConversationListMvi? = null,
    galleryViewModel: GalleryMvi? = null,
    unpublishedViewModel: UnpublishedMvi? = null,
    calendarViewModel: CalendarMvi? = null,
    shortcutListViewModel: ShortcutListMvi? = null,
    nodeInfoViewModel: NodeInfoMvi? = null,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<Destination.Main>(metadata = ListDetailSceneStrategy.listPane()) {
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
    entry<Destination.PostDetail>(metadata = ListDetailSceneStrategy.detailPane()) {
        PostDetailScreen(
            id = it.postId,
            swipeNavigationEnabled = it.swipeNavigationEnabled,
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.ForumList>(metadata = ListDetailSceneStrategy.listPane()) {
        ForumListScreen(
            id = it.userId,
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.UserDetail>(metadata = ListDetailSceneStrategy.listPane()) {
        UserDetailScreen(
            id = it.userId,
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.Settings> {
        SettingsScreen()
    }
    entry<Destination.HashTag>(metadata = ListDetailSceneStrategy.listPane()) {
        HashtagScreen(
            tag = it.tag,
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.UserList>(metadata = ListDetailSceneStrategy.listPane()) {
        UserListScreen(
            type = it.type,
            userId = it.userId,
            postId = it.postId,
            infoCount = it.infoCount,
            enableExport = it.enableExport,
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.Favorites>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: PostListMvi = favoritesViewModel ?: assistedMetroViewModel<PostListViewModel>(
            extras = PostListViewModel.getExtras(type = PostListType.Favorites)
        )
        PostListScreen(
            model = model,
            title = LocalStrings.current.favoritesTitle,
        )
    }
    entry<Destination.Bookmarks>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: PostListMvi = bookmarksViewModel ?: assistedMetroViewModel<PostListViewModel>(
            extras = PostListViewModel.getExtras(type = PostListType.Bookmarks)
        )
        PostListScreen(
            model = model,
            title = LocalStrings.current.bookmarksTitle,
        )
    }
    entry<Destination.QuotingPosts>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: PostListMvi = assistedMetroViewModel<PostListViewModel>(
            extras = PostListViewModel.getExtras(
                type = PostListType.Quoting(
                    postId = it.postId,
                    otherInstance = it.otherInstance,
                ),
            ),
        )
        PostListScreen(
            model = model,
            title = LocalStrings.current.extendedSocialInfoQuotes(it.count),
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.FollowedHashtags>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: FollowedHashtagsMvi = followedHashtagsViewModel ?: metroViewModel<FollowedHashtagsViewModel>()
        FollowedHashtagsScreen(model = model)
    }
    entry<Destination.Composer> {
        ComposerScreen(
            inReplyToId = it.inReplyToId,
            inReplyToUsername = it.inReplyToUsername,
            inReplyToHandle = it.inReplyToHandle,
            quotedId = it.quotedId,
            groupUsername = it.groupUsername,
            groupHandle = it.groupHandle,
            editedPostId = it.editedPostId,
            scheduledPostId = it.scheduledPostId,
            draftId = it.draftId,
            urlToShare = it.urlToShare,
            initialText = it.initialText,
            hasInitialAttachment = it.hasInitialAttachment,
        )
    }
    entry<Destination.Search>(metadata = ListDetailSceneStrategy.listPane()) {
        SearchScreen()
    }
    entry<Destination.Thread>(metadata = ListDetailSceneStrategy.listPane()) {
        ThreadScreen(
            postId = it.postId,
            swipeNavigationEnabled = it.swipeNavigationEnabled,
            otherInstance = it.otherInstance,
        )
    }
    entry<Destination.ImageDetail> {
        ImageDetailScreen(
            urls = it.urls,
            initialIndex = it.initialIndex,
            videoIndices = it.videoIndices,
        )
    }
    entry<Destination.ManageBlocks>(metadata = ListDetailSceneStrategy.listPane()) {
        ManageBlocksScreen()
    }
    entry<Destination.Circles>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: CirclesMvi = circlesViewModel ?: metroViewModel<CirclesViewModel>()
        CirclesScreen(model = model)
    }
    entry<Destination.CircleMembers>(metadata = ListDetailSceneStrategy.detailPane()) {
        CircleMembersScreen(circleId = it.circleId)
    }
    entry<Destination.CircleTimeline>(metadata = ListDetailSceneStrategy.listPane()) {
        CircleTimelineScreen(id = it.circleId)
    }
    entry<Destination.FollowRequests>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: FollowRequestsMvi = followRequestsViewModel ?: metroViewModel<FollowRequestsViewModel>()
        FollowRequestsScreen(model = model)
    }
    entry<Destination.EditProfile> {
        EditProfileScreen()
    }
    entry<Destination.NodeInfo> {
        val model: NodeInfoMvi = nodeInfoViewModel ?: metroViewModel<NodeInfoViewModel>()
        NodeInfoScreen(model = model)
    }
    entry<Destination.ConversationList>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: ConversationListMvi = conversationListViewModel ?: metroViewModel<ConversationListViewModel>()
        ConversationListScreen(model = model)
    }
    entry<Destination.Conversation>(metadata = ListDetailSceneStrategy.detailPane()) {
        ConversationScreen(
            otherUserId = it.otherUserId,
            parentUri = it.parentUri,
        )
    }
    entry<Destination.Gallery>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: GalleryMvi = galleryViewModel ?: metroViewModel<GalleryViewModel>()
        GalleryScreen(model = model)
    }
    entry<Destination.AlbumDetail>(metadata = ListDetailSceneStrategy.detailPane()) {
        AlbumDetailScreen(name = it.name)
    }
    entry<Destination.Unpublished>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: UnpublishedMvi = unpublishedViewModel ?: metroViewModel<UnpublishedViewModel>()
        UnpublishedScreen(model = model)
    }
    entry<Destination.CreateReport> {
        CreateReportScreen(
            userId = it.userId,
            postId = it.postId,
        )
    }
    entry<Destination.UserFeedback> {
        UserFeedbackScreen()
    }
    entry<Destination.Calendar>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: CalendarMvi = calendarViewModel ?: metroViewModel<CalendarViewModel>()
        CalendarScreen(model = model)
    }
    entry<Destination.EventDetail>(metadata = ListDetailSceneStrategy.detailPane()) {
        EventDetailScreen(eventId = it.eventId)
    }
    entry<Destination.Licences> {
        LicencesScreen()
    }
    entry<Destination.WebView> {
        WebViewScreen(url = it.url)
    }
    entry<Destination.Announcements> {
        AnnouncementsScreen()
    }
    entry<Destination.Acknowledgements> {
        AcknowledgementsScreen()
    }
    entry<Destination.ShortcutList>(metadata = ListDetailSceneStrategy.listPane()) {
        val model: ShortcutListMvi = shortcutListViewModel ?: metroViewModel<ShortcutListViewModel>()
        ShortcutListScreen(model = model)
    }
    entry<Destination.ShortcutTimeline>(metadata = ListDetailSceneStrategy.listPane()) {
        ShortcutTimelineScreen(node = it.node)
    }
    entry<Destination.Login> {
        LoginScreen(loginType = it.type)
    }
    entry<Destination.LegacyLogin> {
        LegacyLoginScreen()
    }
    entry<Destination.NewAccount> {
        NewAccountScreen()
    }
    entry<Destination.ManageUserCircles> {
        ManageUserCirclesScreen(userId = it.userId)
    }
    entry<Destination.Explore>(metadata = ListDetailSceneStrategy.listPane()) {
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
    entry<Destination.Inbox>(metadata = ListDetailSceneStrategy.listPane()) {
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
    entry<Destination.Profile>(metadata = ListDetailSceneStrategy.listPane()) {
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
