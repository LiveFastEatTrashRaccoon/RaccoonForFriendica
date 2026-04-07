package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Stable
interface MainRouter {
    fun openUserDetail(user: UserModel, otherInstance: String? = null)

    fun switchUserDetailToClassicMode(user: UserModel, otherInstance: String? = null)

    fun switchUserDetailToForumMode(user: UserModel, otherInstance: String? = null)

    fun openEntryDetail(
        entry: TimelineEntryModel,
        replaceTop: Boolean = false,
        swipeNavigationEnabled: Boolean = false,
        otherInstance: String? = null,
    )

    fun openSettings()

    fun openHashtag(tag: String, otherInstance: String? = null)

    fun openFollowers(user: UserModel, enableExport: Boolean = false, otherInstance: String? = null)

    fun openFollowing(user: UserModel, enableExport: Boolean = false, otherInstance: String? = null)

    fun openFavorites()

    fun openBookmarks()

    fun openFollowedHashtags()

    fun openEntryUsersFavorite(entryId: String, count: Int, otherInstance: String? = null)

    fun openEntryUsersReblog(entryId: String, count: Int, otherInstance: String? = null)

    fun openComposer(
        inReplyTo: TimelineEntryModel? = null,
        inReplyToUser: UserModel? = null,
        editedPostId: String? = null,
        urlToShare: String? = null,
        inGroup: Boolean = false,
        initialText: String? = null,
        initialAttachment: ByteArray? = null,
    )

    fun openEditUnpublished(entry: TimelineEntryModel, type: UnpublishedType)

    fun openSearch()

    fun openThread(entry: TimelineEntryModel, swipeNavigationEnabled: Boolean = false, otherInstance: String? = null)

    fun openImageDetail(url: String)

    fun openImageDetail(urls: List<String>, initialIndex: Int, videoIndices: List<Int> = emptyList())

    fun openBlockedAndMuted()

    fun openCircles()

    fun openCircleEditMembers(groupId: String)

    fun openCircleTimeline(circle: CircleModel)

    fun openFollowRequests()

    fun openEditProfile()

    fun openNodeInfo()

    fun openDirectMessages()

    fun openConversation(otherUser: UserModel, parentUri: String)

    fun openGallery()

    fun openAlbum(name: String)

    fun openUnpublished()

    fun openCreateReport(user: UserModel, entry: TimelineEntryModel? = null)

    fun openUserFeedback()

    fun openCalendar()

    fun openEvent(event: EventModel)

    fun openLicences()

    fun openInternalWebView(url: String)

    fun openAnnouncements()

    fun openAcknowledgements()

    fun openShortcuts()

    fun openShortcut(node: String)

    fun openManageCircles(user: UserModel)
}
