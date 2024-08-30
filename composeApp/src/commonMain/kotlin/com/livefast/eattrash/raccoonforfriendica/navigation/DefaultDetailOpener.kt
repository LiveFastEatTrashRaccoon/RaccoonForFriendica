package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagScreen
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksScreen
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
        val screen = UserListScreen(UserListType.Follower(userId))
        navigationCoordinator.push(screen)
    }

    override fun openFollowing(userId: String) {
        val screen = UserListScreen(UserListType.Following(userId))
        navigationCoordinator.push(screen)
    }

    override fun openFavorites() {
        if (!isLogged) {
            return
        }
        val screen = FavoritesScreen(type = FavoritesType.Favorites)
        navigationCoordinator.push(screen)
    }

    override fun openBookmarks() {
        if (!isLogged) {
            return
        }
        val screen = FavoritesScreen(type = FavoritesType.Bookmarks)
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
        val screen = UserListScreen(UserListType.UsersFavorite(entryId, count))
        navigationCoordinator.push(screen)
    }

    override fun openEntryUsersReblog(
        entryId: String,
        count: Int,
    ) {
        val screen = UserListScreen(UserListType.UsersReblog(entryId, count))
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
}
