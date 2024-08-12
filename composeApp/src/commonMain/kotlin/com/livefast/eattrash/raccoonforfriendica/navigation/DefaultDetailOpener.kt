package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.feature.userdetail.UserDetailScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.HashtagScreen
import com.livefast.eattrash.raccoonforfriendica.feature.login.LoginScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen

class DefaultDetailOpener(
    private val navigationCoordinator: NavigationCoordinator,
) : DetailOpener {
    override fun openUserDetail(id: String) {
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

    override fun openLogin() {
        val screen = LoginScreen()
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
        val screen = FavoritesScreen(type = FavoritesType.Favorites)
        navigationCoordinator.push(screen)
    }

    override fun openBookmarks() {
        val screen = FavoritesScreen(type = FavoritesType.Bookmarks)
        navigationCoordinator.push(screen)
    }

    override fun openFollowedHashtags() {
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
}
