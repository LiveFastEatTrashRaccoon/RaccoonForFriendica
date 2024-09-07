package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
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
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchScreen

class DefaultDetailOpener(
    private val navigationCoordinator: NavigationCoordinator,
    private val identityRepository: IdentityRepository,
) : DetailOpener {
    private val currentUserId: String? get() = identityRepository.currentUser.value?.id
    private val isLogged: Boolean get() = currentUserId != null

    override fun openUserDetail(id: String) {
        if (id == currentUserId) {
            return
        }
        val screen = UserDetailScreen(id)
        navigationCoordinator.push(screen)
    }

    override fun openEntryDetail(id: String) {
        val screen = EntryDetailScreen(id)
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

    override fun openFollowers(userId: String) {
        val screen =
            UserListScreen(
                type = UserListType.Follower.toInt(),
                userId = userId,
            )
        navigationCoordinator.push(screen)
    }

    override fun openFollowing(userId: String) {
        val screen =
            UserListScreen(
                type = UserListType.Following.toInt(),
                userId = userId,
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
        inReplyToUsername: String?,
        inReplyToHandle: String?,
        groupUsername: String?,
        groupHandle: String?,
        editedPostId: String?,
    ) {
        if (!isLogged) {
            return
        }
        val screen =
            ComposerScreen(
                inReplyToId = inReplyToId,
                inReplyToUsername = inReplyToUsername,
                inReplyToHandle = inReplyToHandle,
                groupUsername = groupUsername,
                groupHandle = groupHandle,
                editedPostId = editedPostId,
            )
        navigationCoordinator.push(screen)
    }

    override fun openSearch() {
        val screen = SearchScreen()
        navigationCoordinator.push(screen)
    }

    override fun openInForumMode(groupId: String) {
        val screen = ForumListScreen(groupId)
        navigationCoordinator.push(screen)
    }

    override fun openThread(entryId: String) {
        val screen = ThreadScreen(entryId)
        navigationCoordinator.push(screen)
    }

    override fun openImageDetail(url: String) {
        val screen = ImageDetailScreen(url)
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
        otherUserId: String,
        parentUri: String,
    ) {
        val screen =
            ConversationScreen(
                otherUserId = otherUserId,
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
                createMode = createMode
        )
        navigationCoordinator.push(screen)
    }
}
