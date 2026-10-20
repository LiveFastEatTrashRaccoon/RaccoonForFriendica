package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserListType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toInt
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.AttachmentCache
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.cache.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultMainRouter(
    private val navigationCoordinator: NavigationCoordinator,
    private val identityRepository: IdentityRepository,
    private val settingsRepository: SettingsRepository,
    private val userCache: LocalItemCache<UserModel>,
    private val postCache: LocalItemCache<PostModel>,
    private val eventCache: LocalItemCache<EventModel>,
    private val circleCache: LocalItemCache<CircleModel>,
    private val attachmentCache: AttachmentCache,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MainRouter {
    private val currentUserId: String? get() = identityRepository.currentUser.value?.id
    private val isLogged: Boolean get() = currentUserId != null
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun openUserDetail(user: UserModel, otherInstance: String?) {
        if (user.id == currentUserId) { return }
        val openGroupsInForumModeByDefault =
            settingsRepository.current.value?.openGroupsInForumModeByDefault == true
        scope.launch {
            userCache.put(user.id, user)
        }
        val destination =
            if (user.group && openGroupsInForumModeByDefault) {
                Destination.ForumList(userId = user.id, otherInstance = otherInstance)
            } else {
                Destination.UserDetail(userId = user.id, otherInstance = otherInstance)
            }
        navigationCoordinator.push(destination)
    }

    override fun switchUserDetailToClassicMode(user: UserModel, otherInstance: String?) {
        navigationCoordinator.replace(Destination.UserDetail(userId = user.id, otherInstance = otherInstance))
    }

    override fun switchUserDetailToForumMode(user: UserModel, otherInstance: String?) {
        navigationCoordinator.replace(Destination.ForumList(userId = user.id, otherInstance = otherInstance))
    }

    override fun openPostDetail(post: PostModel, replaceTop: Boolean, swipeNavigationEnabled: Boolean, otherInstance: String?) {
        scope.launch {
            postCache.put(post.id, post)
        }
        val destination =
            Destination.PostDetail(
                postId = post.id,
                swipeNavigationEnabled = swipeNavigationEnabled,
                otherInstance = otherInstance,
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

    override fun openHashtag(tag: String, otherInstance: String?) {
        navigationCoordinator.push(Destination.HashTag(tag = tag, otherInstance = otherInstance))
    }

    override fun openFollowers(user: UserModel, enableExport: Boolean, otherInstance: String?) {
        scope.launch {
            userCache.put(user.id, user)
        }
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.Follower.toInt(),
                userId = user.id,
                enableExport = enableExport,
                otherInstance = otherInstance,
            ),
        )
    }

    override fun openFollowing(user: UserModel, enableExport: Boolean, otherInstance: String?) {
        scope.launch {
            userCache.put(user.id, user)
        }
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.Following.toInt(),
                userId = user.id,
                enableExport = enableExport,
                otherInstance = otherInstance,
            ),
        )
    }

    override fun openFavorites() {
        if (!isLogged) { return }
        navigationCoordinator.push(Destination.Favorites)
    }

    override fun openBookmarks() {
        if (!isLogged) { return }
        navigationCoordinator.push(Destination.Bookmarks)
    }

    override fun openFollowedHashtags() {
        if (!isLogged) { return }
        navigationCoordinator.push(Destination.FollowedHashtags)
    }

    override fun openPostUsersFavorite(postId: String, count: Int, otherInstance: String?) {
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.UsersFavorite.toInt(),
                postId = postId,
                infoCount = count,
                otherInstance = otherInstance,
            ),
        )
    }

    override fun openPostUsersReblog(postId: String, count: Int, otherInstance: String?) {
        navigationCoordinator.push(
            Destination.UserList(
                type = UserListType.UsersReblog.toInt(),
                postId = postId,
                infoCount = count,
                otherInstance = otherInstance,
            ),
        )
    }

    override fun openComposer(
        inReplyTo: PostModel?,
        inReplyToUser: UserModel?,
        editedPostId: String?,
        quoted: PostModel?,
        urlToShare: String?,
        inGroup: Boolean,
        initialText: String?,
        initialAttachment: ByteArray?,
    ) {
        if (!isLogged) { return }
        scope.launch {
            if (inReplyTo != null) {
                postCache.put(inReplyTo.id, inReplyTo)
            }
            if (inReplyToUser != null) {
                userCache.put(inReplyToUser.id, inReplyToUser)
            }
            if (quoted != null) {
                postCache.put(quoted.id, quoted)
            }
        }
        val isGroup = inReplyToUser?.group == true && inGroup
        if (initialAttachment != null) {
            attachmentCache.put(initialAttachment)
        }
        navigationCoordinator.push(
            Destination.Composer(
                inReplyToId = inReplyTo?.id,
                inReplyToUsername = inReplyToUser?.username.takeIf { !isGroup },
                inReplyToHandle = inReplyToUser?.handle.takeIf { !isGroup },
                quotedId = quoted?.id,
                groupUsername = inReplyToUser?.username.takeIf { isGroup },
                groupHandle = inReplyToUser?.handle.takeIf { isGroup },
                editedPostId = editedPostId,
                urlToShare = urlToShare,
                initialText = initialText,
                hasInitialAttachment = initialAttachment != null,
            ),
        )
    }

    override fun openEditUnpublished(post: PostModel, type: UnpublishedType) {
        scope.launch {
            postCache.put(post.id, post)
        }
        navigationCoordinator.push(
            Destination.Composer(
                scheduledPostId = post.id.takeIf { type == UnpublishedType.Scheduled },
                draftId = post.id.takeIf { type == UnpublishedType.Drafts },
            ),
        )
    }

    override fun openSearch() {
        navigationCoordinator.push(Destination.Search)
    }

    override fun openThread(post: PostModel, swipeNavigationEnabled: Boolean, otherInstance: String?) {
        scope.launch {
            postCache.put(post.id, post)
        }
        navigationCoordinator.push(
            Destination.Thread(
                postId = post.id,
                swipeNavigationEnabled = swipeNavigationEnabled,
                otherInstance = otherInstance,
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

    override fun openCreateReport(user: UserModel, post: PostModel?) {
        scope.launch {
            userCache.put(user.id, user)
            if (post != null) {
                postCache.put(post.id, post)
            }
        }
        navigationCoordinator.push(
            Destination.CreateReport(
                userId = user.id,
                postId = post?.id,
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

    override fun openManageCircles(user: UserModel) {
        scope.launch {
            userCache.put(user.id, user)
        }
        navigationCoordinator.push(Destination.ManageUserCircles(user.id))
    }
}
