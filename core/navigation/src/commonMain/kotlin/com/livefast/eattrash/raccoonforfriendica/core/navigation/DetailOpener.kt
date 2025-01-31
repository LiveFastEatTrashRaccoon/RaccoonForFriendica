package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Stable
interface DetailOpener {
    fun openUserDetail(user: UserModel)

    fun switchUserDetailToClassicMode(user: UserModel)

    fun switchUserDetailToForumMode(user: UserModel)

    fun openEntryDetail(
        entry: TimelineEntryModel,
        replaceTop: Boolean = false,
        swipeNavigationEnabled: Boolean = false,
    )

    fun openSettings()

    fun openHashtag(tag: String)

    fun openFollowers(
        user: UserModel,
        enableExport: Boolean = false,
    )

    fun openFollowing(
        user: UserModel,
        enableExport: Boolean = false,
    )

    fun openFavorites()

    fun openBookmarks()

    fun openFollowedHashtags()

    fun openEntryUsersFavorite(
        entryId: String,
        count: Int,
    )

    fun openEntryUsersReblog(
        entryId: String,
        count: Int,
    )

    fun openComposer(
        inReplyTo: TimelineEntryModel? = null,
        inReplyToUser: UserModel? = null,
        editedPostId: String? = null,
        urlToShare: String? = null,
        inGroup: Boolean = false,
        initialText: String? = null,
        initialAttachment: ByteArray? = null,
    )

    fun openEditUnpublished(
        entry: TimelineEntryModel,
        type: UnpublishedType,
    )

    fun openSearch()

    fun openThread(
        entry: TimelineEntryModel,
        swipeNavigationEnabled: Boolean = false,
    )

    fun openImageDetail(url: String)

    fun openImageDetail(
        urls: List<String>,
        initialIndex: Int,
        videoIndices: List<Int> = emptyList(),
    )

    fun openBlockedAndMuted()

    fun openCircles()

    fun openCircleEditMembers(groupId: String)

    fun openCircleTimeline(circle: CircleModel)

    fun openFollowRequests()

    fun openEditProfile()

    fun openNodeInfo()

    fun openDirectMessages()

    fun openConversation(
        otherUser: UserModel,
        parentUri: String,
    )

    fun openGallery()

    fun openAlbum(name: String)

    fun openUnpublished()

    fun openCreateReport(
        user: UserModel,
        entry: TimelineEntryModel? = null,
    )

    fun openUserFeedback()

    fun openCalendar()

    fun openEvent(event: EventModel)

    fun openLicences()

    fun openInternalWebView(url: String)

    fun openAnnouncements()

    fun openAcknowledgements()

    fun openShortcuts()

    fun openShortcut(node: String)
}
