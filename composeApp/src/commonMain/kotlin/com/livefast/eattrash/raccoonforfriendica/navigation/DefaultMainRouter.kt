package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FavoritesType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DefaultMainRouter(
    private val navigationCoordinator: NavigationCoordinator,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val entryCache: LocalItemCache<TimelineEntryModel>,
    private val eventCache: LocalItemCache<EventModel>,
    private val circleCache: LocalItemCache<CircleModel>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MainRouter {
    private val currentUserId: String? get() = identityRepository.currentUser.value?.id
    private val isLogged: Boolean get() = currentUserId != null
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun openUserDetail(user: UserModel) {
        check(user.id != currentUserId) { return }
        val openGroupsInForumModeByDefault =
            settingsRepository.current.value?.openGroupsInForumModeByDefault == true
        scope.launch {
            userCache.put(user.id, user)
        }
        val destination =
            if (user.group && openGroupsInForumModeByDefault) {
                Destination.ForumList(user.id)
            } else {
                Destination.UserDetail(user.id)
            }
        navigationCoordinator.push(destination)
    }

    override fun switchUserDetailToClassicMode(user: UserModel) {
        navigationCoordinator.replace(Destination.UserDetail(user.id))
    }

    override fun switchUserDetailToForumMode(user: UserModel) {
        navigationCoordinator.replace(Destination.ForumList(user.id))
    }

    override fun openEntryDetail(entry: TimelineEntryModel, replaceTop: Boolean, swipeNavigationEnabled: Boolean) {
        scope.launch {
            entryCache.put(entry.id, entry)
        }
        val destination =
            Destination.EntryDetail(
                entryId = entry.id,
                swipeNavigationEnabled = swipeNavigationEnabled,
            )
        if (replaceTop) {
            navigationCoordinator.replace(destination)
        } else {
            navigationCoordinator.push(destination)
        }
    }

    override fun openSettings() {
        navigationCoordinator.push(Destination.Settings)
    }

    override fun openHashtag(tag: String) {
        navigationCoordinator.push(Destination.HashTag(tag))
    }

    override fun openFollowers(user: UserModel, enableExport: Boolean) {
        scope.launch {
            userCache.put(user.id, user)
        }
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.Follower.toInt(),
                userId = user.id,
                enableExport = enableExport,
            ),
        )
    }

    override fun openFollowing(user: UserModel, enableExport: Boolean) {
        scope.launch {
            userCache.put(user.id, user)
        }
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.Following.toInt(),
                userId = user.id,
                enableExport = enableExport,
            ),
        )
    }

    override fun openFavorites() {
        check(isLogged) { return }
        navigationCoordinator.push(Destination.Favorites(type = FavoritesType.Favorites.toInt()))
    }

    override fun openBookmarks() {
        check(isLogged) { return }
        navigationCoordinator.push(Destination.Favorites(type = FavoritesType.Bookmarks.toInt()))
    }

    override fun openFollowedHashtags() {
        check(isLogged) { return }
        navigationCoordinator.push(Destination.FollowedHashtags)
    }

    override fun openEntryUsersFavorite(entryId: String, count: Int) {
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.UsersFavorite.toInt(),
                entryId = entryId,
                infoCount = count,
            ),
        )
    }

    override fun openEntryUsersReblog(entryId: String, count: Int) {
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.UsersReblog.toInt(),
                entryId = entryId,
                infoCount = count,
            ),
        )
    }

    override fun openComposer(
        inReplyTo: TimelineEntryModel?,
        inReplyToUser: UserModel?,
        editedPostId: String?,
        urlToShare: String?,
        inGroup: Boolean,
        initialText: String?,
        initialAttachment: ByteArray?,
    ) {
        check(isLogged) { return }
        scope.launch {
            if (inReplyTo != null) {
                entryCache.put(inReplyTo.id, inReplyTo)
            }
            if (inReplyToUser != null) {
                userCache.put(inReplyToUser.id, inReplyToUser)
            }
        }
        val isGroup = inReplyToUser?.group == true && inGroup
        // TODO: handle initialAttachment with cache
        navigationCoordinator.push(
            Destination.Composer(
                inReplyToId = inReplyTo?.id,
                inReplyToUsername = inReplyToUser?.username.takeIf { !isGroup },
                inReplyToHandle = inReplyToUser?.handle.takeIf { !isGroup },
                groupUsername = inReplyToUser?.username.takeIf { isGroup },
                groupHandle = inReplyToUser?.handle.takeIf { isGroup },
                editedPostId = editedPostId,
                urlToShare = urlToShare,
                initialText = initialText,
            ),
        )
    }

    override fun openEditUnpublished(entry: TimelineEntryModel, type: UnpublishedType) {
        scope.launch {
            entryCache.put(entry.id, entry)
        }
        navigationCoordinator.push(
            Destination.Composer(
                scheduledPostId = entry.id.takeIf { type == UnpublishedType.Scheduled },
                draftId = entry.id.takeIf { type == UnpublishedType.Drafts },
            ),
        )
    }

    override fun openSearch() {
        navigationCoordinator.push(Destination.Search)
    }

    override fun openThread(entry: TimelineEntryModel, swipeNavigationEnabled: Boolean) {
        scope.launch {
            entryCache.put(entry.id, entry)
        }
        navigationCoordinator.push(
            Destination.Thread(
                entryId = entry.id,
                swipeNavigationEnabled = swipeNavigationEnabled,
            ),
        )
    }

    override fun openImageDetail(url: String) {
        openImageDetail(
            urls = listOf(url),
            initialIndex = 0,
        )
    }

    override fun openImageDetail(urls: List<String>, initialIndex: Int, videoIndices: List<Int>) {
        navigationCoordinator.push(
            Destination.ImageDetail(
                urls = urls,
                initialIndex = initialIndex,
                videoIndices = videoIndices,
            ),
        )
    }

    override fun openBlockedAndMuted() {
        navigationCoordinator.push(Destination.ManageBlocks)
    }

    override fun openCircles() {
        navigationCoordinator.push(Destination.Circles)
    }

    override fun openCircleEditMembers(groupId: String) {
        navigationCoordinator.push(Destination.CircleMembers(groupId))
    }

    override fun openCircleTimeline(circle: CircleModel) {
        scope.launch {
            circleCache.put(circle.id, circle)
        }
        navigationCoordinator.push(Destination.CircleTimeline(circle.id))
    }

    override fun openFollowRequests() {
        navigationCoordinator.push(Destination.FollowRequests)
    }

    override fun openEditProfile() {
        navigationCoordinator.push(Destination.EditProfile)
    }

    override fun openNodeInfo() {
        navigationCoordinator.push(Destination.NodeInfo)
    }

    override fun openDirectMessages() {
        navigationCoordinator.push(Destination.ConversationList)
    }

    override fun openConversation(otherUser: UserModel, parentUri: String) {
        scope.launch {
            userCache.put(otherUser.id, otherUser)
        }
        navigationCoordinator.push(
            Destination.Conversation(
                otherUserId = otherUser.id,
                parentUri = parentUri,
            ),
        )
    }

    override fun openGallery() {
        navigationCoordinator.push(Destination.Gallery)
    }

    override fun openAlbum(name: String) {
        navigationCoordinator.push(
            Destination.AlbumDetail(
                name = name,
            ),
        )
    }

    override fun openUnpublished() {
        navigationCoordinator.push(Destination.Unpublished)
    }

    override fun openCreateReport(user: UserModel, entry: TimelineEntryModel?) {
        scope.launch {
            userCache.put(user.id, user)
            if (entry != null) {
                entryCache.put(entry.id, entry)
            }
        }
        navigationCoordinator.push(
            Destination.CreateReport(
                userId = user.id,
                entryId = entry?.id,
            ),
        )
    }

    override fun openUserFeedback() {
        navigationCoordinator.push(Destination.UserFeedback)
    }

    override fun openCalendar() {
        navigationCoordinator.push(Destination.Calendar)
    }

    override fun openEvent(event: EventModel) {
        scope.launch {
            eventCache.put(event.id, event)
        }
        navigationCoordinator.push(Destination.EventDetail(event.id))
    }

    override fun openLicences() {
        navigationCoordinator.push(Destination.Licences)
    }

    override fun openInternalWebView(url: String) {
        navigationCoordinator.push(Destination.WebView(url))
    }

    override fun openAnnouncements() {
        navigationCoordinator.push(Destination.Announcements)
    }

    override fun openAcknowledgements() {
        navigationCoordinator.push(Destination.Acknowledgements)
    }

    override fun openShortcuts() {
        navigationCoordinator.push(Destination.ShortcutList)
    }

    override fun openShortcut(node: String) {
        navigationCoordinator.push(Destination.ShortcutTimeline(node))
    }
}
