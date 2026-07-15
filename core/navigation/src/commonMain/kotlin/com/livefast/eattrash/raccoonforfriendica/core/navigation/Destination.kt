package com.livefast.eattrash.raccoonforfriendica.core.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Main : Destination

    @Serializable
    data class EntryDetail(
        val entryId: String,
        val swipeNavigationEnabled: Boolean,
        val otherInstance: String? = null,
    ) : Destination

    @Serializable
    data class ForumList(val userId: String, val otherInstance: String? = null) : Destination

    @Serializable
    data class UserDetail(val userId: String, val otherInstance: String? = null) : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data class HashTag(val tag: String, val otherInstance: String? = null) : Destination

    @Serializable
    data class UserList(
        val type: Int,
        val userId: String? = null,
        val entryId: String? = null,
        val enableExport: Boolean = false,
        val infoCount: Int? = null,
        val otherInstance: String? = null,
    ) : Destination

    @Serializable
    data object Favorites : Destination

    @Serializable
    data object Bookmarks : Destination

    @Serializable
    data class QuotingEntries(val entryId: String, val count: Int, val otherInstance: String? = null) : Destination

    @Serializable
    data object FollowedHashtags : Destination

    @Serializable
    data class Composer(
        val inReplyToId: String? = null,
        val inReplyToUsername: String? = null,
        val inReplyToHandle: String? = null,
        val quotedId: String? = null,
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
    data class Thread(val entryId: String, val swipeNavigationEnabled: Boolean, val otherInstance: String? = null) :
        Destination

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

    @Serializable
    data class ManageUserCircles(val userId: String) : Destination

    @Serializable
    data object Explore : Destination

    @Serializable
    data object Inbox : Destination

    @Serializable
    data object Profile : Destination

    companion object {

        val SavedStateConfiguration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Main::class, Main.serializer())
                    subclass(EntryDetail::class, EntryDetail.serializer())
                    subclass(ForumList::class, ForumList.serializer())
                    subclass(UserDetail::class, UserDetail.serializer())
                    subclass(Settings::class, Settings.serializer())
                    subclass(HashTag::class, HashTag.serializer())
                    subclass(UserList::class, UserList.serializer())
                    subclass(Favorites::class, Favorites.serializer())
                    subclass(Bookmarks::class, Bookmarks.serializer())
                    subclass(QuotingEntries::class, QuotingEntries.serializer())
                    subclass(FollowedHashtags::class, FollowedHashtags.serializer())
                    subclass(Composer::class, Composer.serializer())
                    subclass(Search::class, Search.serializer())
                    subclass(Thread::class, Thread.serializer())
                    subclass(ImageDetail::class, ImageDetail.serializer())
                    subclass(ManageBlocks::class, ManageBlocks.serializer())
                    subclass(Circles::class, Circles.serializer())
                    subclass(CircleMembers::class, CircleMembers.serializer())
                    subclass(CircleTimeline::class, CircleTimeline.serializer())
                    subclass(FollowRequests::class, FollowRequests.serializer())
                    subclass(EditProfile::class, EditProfile.serializer())
                    subclass(NodeInfo::class, NodeInfo.serializer())
                    subclass(Conversation::class, Conversation.serializer())
                    subclass(Gallery::class, Gallery.serializer())
                    subclass(AlbumDetail::class, AlbumDetail.serializer())
                    subclass(Unpublished::class, Unpublished.serializer())
                    subclass(CreateReport::class, CreateReport.serializer())
                    subclass(UserFeedback::class, UserFeedback.serializer())
                    subclass(Calendar::class, Calendar.serializer())
                    subclass(EventDetail::class, EventDetail.serializer())
                    subclass(Licences::class, Licences.serializer())
                    subclass(WebView::class, WebView.serializer())
                    subclass(Announcements::class, Announcements.serializer())
                    subclass(Acknowledgements::class, Acknowledgements.serializer())
                    subclass(ShortcutList::class, ShortcutList.serializer())
                    subclass(ShortcutTimeline::class, ShortcutTimeline.serializer())
                    subclass(Login::class, Login.serializer())
                    subclass(LegacyLogin::class, LegacyLogin.serializer())
                    subclass(NewAccount::class, NewAccount.serializer())
                    subclass(ManageUserCircles::class, ManageUserCircles.serializer())
                    subclass(Explore::class, Explore.serializer())
                    subclass(Inbox::class, Inbox.serializer())
                    subclass(Profile::class, Profile.serializer())
                }
            }
        }

        val Saver = Saver<Destination, String>(
            save = { Json.encodeToString(it) },
            restore = { Json.decodeFromString<Destination>(it) },
        )
    }
}
