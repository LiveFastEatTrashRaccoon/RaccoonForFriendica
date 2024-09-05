package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.Stable

@Stable
interface DetailOpener {
    fun openUserDetail(id: String)

    fun openEntryDetail(id: String)

    fun openSettings()

    fun openHashtag(tag: String)

    fun openFollowers(userId: String)

    fun openFollowing(userId: String)

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
        inReplyToUsername: String? = null,
        inReplyToHandle: String? = null,
        groupUsername: String? = null,
        groupHandle: String? = null,
        editedPostId: String? = null,
    )

    fun openSearch()

    fun openInForumMode(groupId: String)

    fun openThread(entryId: String)

    fun openImageDetail(url: String)

    fun openBlockedAndMuted()

    fun openCircles()

    fun openCircle(groupId: String)

    fun openFollowRequests()

    fun openEditProfile()

    fun openNodeInfo()

    fun openDirectMessages()

    fun openConversation(
        otherUserId: String,
        parentUri: String,
    )

    fun openGallery()

    fun openAlbum(
        name: String,
        createMode: Boolean = false,
    )
}
