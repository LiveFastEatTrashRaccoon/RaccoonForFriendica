package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.DirectMessageListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagScreen
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksScreen
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackScreen
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadScreen
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchScreen

class DefaultDetailOpener(
    private val navigationCoordinator: NavigationCoordinator,
    private val identityRepository: IdentityRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
) : DetailOpener {
    private val currentUserId: String? get() = identityRepository.currentUser.value?.id
    private val isLogged: Boolean get() = currentUserId != null

    override fun openUserDetail(user: UserModel) {
        if (user.id == currentUserId) {
            return
        }
        userCache.put(user.id, user)
        val screen = UserDetailScreen(user.id)
        navigationCoordinator.push(screen)
    }

    override fun openEntryDetail(entry: TimelineEntryModel) {
        entryCache.put(entry.id, entry)
        val screen = EntryDetailScreen(entry.id)
        navigationCoordinator.push(screen)
    }

    override fun openSettings() {
        val screen = SettingsScreen()
        navigationCoordinator.push(screen)
    }

    override fun openHashtag(tag: String) {
        val screen = HashtagScreen(tag)
        navigationCoordinator.push(screen)
    }

    override fun openFollowers(user: UserModel) {
        userCache.put(user.id, user)
        val screen =
            UserListScreen(
                type = UserListType.Follower.toInt(),
                userId = user.id,
            )
        navigationCoordinator.push(screen)
    }

    override fun openFollowing(user: UserModel) {
        userCache.put(user.id, user)
        val screen =
            UserListScreen(
                type = UserListType.Following.toInt(),
                userId = user.id,
            )
        navigationCoordinator.push(screen)
    }

    override fun openFavorites() {
        if (!isLogged) {
            return
        }
        val screen = FavoritesScreen(type = FavoritesType.Favorites.toInt())
        navigationCoordinator.push(screen)
    }

    override fun openBookmarks() {
        if (!isLogged) {
            return
        }
        val screen = FavoritesScreen(type = FavoritesType.Bookmarks.toInt())
        navigationCoordinator.push(screen)
    }

    override fun openFollowedHashtags() {
        if (!isLogged) {
            return
        }
        val screen = FollowedHashtagsScreen()
        navigationCoordinator.push(screen)
    }

    override fun openEntryUsersFavorite(
        entryId: String,
        count: Int,
    ) {
        val screen =
            UserListScreen(
                type = UserListType.UsersFavorite.toInt(),
                entryId = entryId,
                infoCount = count,
            )
        navigationCoordinator.push(screen)
    }

    override fun openEntryUsersReblog(
        entryId: String,
        count: Int,
    ) {
        val screen =
            UserListScreen(
                type = UserListType.UsersReblog.toInt(),
                entryId = entryId,
                infoCount = count,
            )
        navigationCoordinator.push(screen)
    }

    override fun openComposer(
        inReplyToId: String?,
        inReplyToUser: UserModel?,
        editedPostId: String?,
        urlToShare: String?,
        inGroup: Boolean,
    ) {
        if (!isLogged) {
            return
        }
        if (inReplyToUser != null) {
            userCache.put(inReplyToUser.id, inReplyToUser)
        }
        val isGroup = inReplyToUser?.group == true && inGroup
        val screen =
            ComposerScreen(
                inReplyToId = inReplyToId,
                inReplyToUsername = inReplyToUser?.username.takeIf { !isGroup },
                inReplyToHandle = inReplyToUser?.handle.takeIf { !isGroup },
                groupUsername = inReplyToUser?.username.takeIf { isGroup },
                groupHandle = inReplyToUser?.handle.takeIf { isGroup },
                editedPostId = editedPostId,
                urlToShare = urlToShare,
            )
        navigationCoordinator.push(screen)
    }

    override fun openEditUnpublished(
        entry: TimelineEntryModel,
        type: UnpublishedType,
    ) {
        entryCache.put(entry.id, entry)
        val screen =
            ComposerScreen(
                scheduledPostId = entry.id.takeIf { type == UnpublishedType.Scheduled },
                draftId = entry.id.takeIf { type == UnpublishedType.Drafts },
            )
        navigationCoordinator.push(screen)
    }

    override fun openSearch() {
        val screen = SearchScreen()
        navigationCoordinator.push(screen)
    }

    override fun openInForumMode(group: UserModel) {
        userCache.put(group.id, group)
        val screen = ForumListScreen(group.id)
        navigationCoordinator.push(screen)
    }

    override fun openThread(entry: TimelineEntryModel) {
        entryCache.put(entry.id, entry)
        val screen = ThreadScreen(entry.id)
        navigationCoordinator.push(screen)
    }

    override fun openImageDetail(url: String) {
        openImageDetail(
            urls = listOf(url),
            initialIndex = 0,
        )
    }

    override fun openImageDetail(
        urls: List<String>,
        initialIndex: Int,
        videoIndices: List<Int>,
    ) {
        val screen =
            ImageDetailScreen(
                urls = urls,
                initialIndex = initialIndex,
                videoIndices = videoIndices,
            )
        navigationCoordinator.push(screen)
    }

    override fun openBlockedAndMuted() {
        val screen = ManageBlocksScreen()
        navigationCoordinator.push(screen)
    }

    override fun openCircles() {
        val screen = CirclesScreen()
        navigationCoordinator.push(screen)
    }

    override fun openCircle(groupId: String) {
        val screen = CircleDetailScreen(groupId)
        navigationCoordinator.push(screen)
    }

    override fun openFollowRequests() {
        val screen = FollowRequestsScreen()
        navigationCoordinator.push(screen)
    }

    override fun openEditProfile() {
        val screen = EditProfileScreen()
        navigationCoordinator.push(screen)
    }

    override fun openNodeInfo() {
        val screen = NodeInfoScreen()
        navigationCoordinator.push(screen)
    }

    override fun openDirectMessages() {
        val screen = DirectMessageListScreen()
        navigationCoordinator.push(screen)
    }

    override fun openConversation(
        otherUser: UserModel,
        parentUri: String,
    ) {
        userCache.put(otherUser.id, otherUser)
        val screen =
            ConversationScreen(
                otherUserId = otherUser.id,
                parentUri = parentUri,
            )
        navigationCoordinator.push(screen)
    }

    override fun openGallery() {
        val screen = GalleryScreen()
        navigationCoordinator.push(screen)
    }

    override fun openAlbum(
        name: String,
        createMode: Boolean,
    ) {
        val screen =
            AlbumDetailScreen(
                name = name,
                createMode = createMode,
            )
        navigationCoordinator.push(screen)
    }

    override fun openUnpublished() {
        val screen = UnpublishedScreen()
        navigationCoordinator.push(screen)
    }

    override fun openCreateReport(
        user: UserModel,
        entry: TimelineEntryModel?,
    ) {
        userCache.put(user.id, user)
        if (entry != null) {
            entryCache.put(entry.id, entry)
        }
        val screen =
            CreateReportScreen(
                userId = user.id,
                entryId = entry?.id,
            )
        navigationCoordinator.push(screen)
    }

    override fun openUserFeedback() {
        val screen = UserFeedbackScreen()
        navigationCoordinator.push(screen)
    }
}
