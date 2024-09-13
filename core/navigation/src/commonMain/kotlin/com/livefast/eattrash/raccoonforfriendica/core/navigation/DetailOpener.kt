package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

@Stable
interface DetailOpener {
    fun openUserDetail(user: UserModel)

    fun openEntryDetail(entry: TimelineEntryModel)

    fun openSettings()

    fun openHashtag(tag: String)

    fun openFollowers(user: UserModel)

    fun openFollowing(user: UserModel)

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
        inReplyToId: String? = null,
        inReplyToUser: UserModel? = null,
        editedPostId: String? = null,
        inGroup: Boolean = false,
    )

    fun openEditUnpublished(
        entry: TimelineEntryModel,
        type: UnpublishedType,
    )

    fun openSearch()

    fun openInForumMode(group: UserModel)

    fun openThread(entry: TimelineEntryModel)

    fun openImageDetail(url: String)

    fun openImageDetail(
        urls: List<String>,
        initialIndex: Int,
    )

    fun openBlockedAndMuted()

    fun openCircles()

    fun openCircle(groupId: String)

    fun openFollowRequests()

    fun openEditProfile()

    fun openNodeInfo()

    fun openDirectMessages()

    fun openConversation(
        otherUser: UserModel,
        parentUri: String,
    )

    fun openGallery()

    fun openAlbum(
        name: String,
        createMode: Boolean = false,
    )

    fun openUnpublished()
}
