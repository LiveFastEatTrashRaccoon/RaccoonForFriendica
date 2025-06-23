package com.livefast.eattrash.raccoonforfriendica.core.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Main : Destination

    @Serializable
    data class EntryDetail(val entryId: String, val swipeNavigationEnabled: Boolean) : Destination

    @Serializable
    data class ForumList(val userId: String) : Destination

    @Serializable
    data class UserDetail(val userId: String) : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data class HashTag(val tag: String) : Destination

    @Serializable
    data class UserList(
        val type: Int,
        val userId: String? = null,
        val entryId: String? = null,
        val enableExport: Boolean = false,
        val infoCount: Int? = null,
    ) : Destination

    @Serializable
    data class Favorites(val type: Int) : Destination

    @Serializable
    data object FollowedHashtags : Destination

    @Serializable
    data class Composer(
        val inReplyToId: String? = null,
        val inReplyToUsername: String? = null,
        val inReplyToHandle: String? = null,
        val groupUsername: String? = null,
        val groupHandle: String? = null,
        val editedPostId: String? = null,
        val scheduledPostId: String? = null,
        val draftId: String? = null,
        val urlToShare: String? = null,
        val initialText: String? = null,
    ) : Destination

    @Serializable
    data object Search : Destination

    @Serializable
    data class Thread(val entryId: String, val swipeNavigationEnabled: Boolean) : Destination

    @Serializable
    data class ImageDetail(
        val urls: List<String>,
        val initialIndex: Int = 0,
        val videoIndices: List<Int> = emptyList(),
    ) : Destination

    @Serializable
    data object ManageBlocks : Destination

    @Serializable
    data object Circles : Destination

    @Serializable
    data class CircleMembers(val circleId: String) : Destination

    @Serializable
    data class CircleTimeline(val circleId: String) : Destination

    @Serializable
    data object FollowRequests : Destination

    @Serializable
    data object EditProfile : Destination

    @Serializable
    data object NodeInfo : Destination

    @Serializable
    data object ConversationList : Destination

    @Serializable
    data class Conversation(val otherUserId: String, val parentUri: String) : Destination

    @Serializable
    data object Gallery : Destination

    @Serializable
    data class AlbumDetail(val name: String) : Destination

    @Serializable
    data object Unpublished : Destination

    @Serializable
    data class CreateReport(val userId: String, val entryId: String? = null) : Destination

    @Serializable
    data object UserFeedback : Destination

    @Serializable
    data object Calendar : Destination

    @Serializable
    data class EventDetail(val eventId: String) : Destination

    @Serializable
    data object Licences : Destination

    @Serializable
    data class WebView(val url: String) : Destination

    @Serializable
    data object Announcements : Destination

    @Serializable
    data object Acknowledgements : Destination

    @Serializable
    data object ShortcutList : Destination

    @Serializable
    data class ShortcutTimeline(val node: String) : Destination

    @Serializable
    data class Login(val type: Int) : Destination

    @Serializable
    data object LegacyLogin : Destination

    @Serializable
    data object NewAccount : Destination
}
