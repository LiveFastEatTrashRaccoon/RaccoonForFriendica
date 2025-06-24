package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.WebViewScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.Destination
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
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main.AcknowledgementsScreen
import com.livefast.eattrash.raccoonforfriendica.feat.licences.LicencesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.announcements.AnnouncementsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.detail.EventDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.calendar.list.CalendarScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.editmembers.CircleMembersScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.timeline.CircleTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.ConversationListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.favorites.FavoritesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.followrequests.FollowRequestsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.detail.AlbumDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.gallery.list.GalleryScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.followed.FollowedHashtagsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.hashtag.timeline.HashtagScreen
import com.livefast.eattrash.raccoonforfriendica.feature.imagedetail.ImageDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.ManageBlocksScreen
import com.livefast.eattrash.raccoonforfriendica.feature.nodeinfo.NodeInfoScreen
import com.livefast.eattrash.raccoonforfriendica.feature.profile.edit.EditProfileScreen
import com.livefast.eattrash.raccoonforfriendica.feature.report.CreateReportScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.SettingsScreen
import com.livefast.eattrash.raccoonforfriendica.feature.settings.feedback.UserFeedbackScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListScreen
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.timeline.ShortcutTimelineScreen
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadScreen
import com.livefast.eattrash.raccoonforfriendica.feature.unpublished.UnpublishedScreen
import com.livefast.eattrash.raccoonforfriendica.feature.userlist.UserListScreen
import com.livefast.eattrash.raccoonforfriendica.feaure.search.SearchScreen
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultMainRouterTest {
    private val navigationCoordinator =
        mock<NavigationCoordinator>(mode = MockMode.autoUnit)
    private val identityRepository =
        mock<IdentityRepository>(mode = MockMode.autoUnit) {
            every { currentUser } returns MutableStateFlow(UserModel(id = "0"))
        }
    private val settingsRepository =
        mock<SettingsRepository>(mode = MockMode.autoUnit) {
            every { current } returns MutableStateFlow(SettingsModel())
        }
    private val userCache = mock<LocalItemCache<UserModel>>(mode = MockMode.autoUnit)
    private val entryCache = mock<LocalItemCache<TimelineEntryModel>>(mode = MockMode.autoUnit)
    private val eventCache = mock<LocalItemCache<EventModel>>(mode = MockMode.autoUnit)
    private val circleCache = mock<LocalItemCache<CircleModel>>(mode = MockMode.autoUnit)
    private val sut =
        DefaultMainRouter(
            navigationCoordinator = navigationCoordinator,
            identityRepository = identityRepository,
            settingsRepository = settingsRepository,
            userCache = userCache,
            entryCache = entryCache,
            eventCache = eventCache,
            circleCache = circleCache,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given individual user when openUserDetail then interactions are as expected`() {
        sut.openUserDetail(user = UserModel(id = "1"))

        verify {
            navigationCoordinator.push(Destination.UserDetail("1"))
        }
    }

    @Test
    fun `given group user when openUserDetail then interactions are as expected`() {
        sut.openUserDetail(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.push(Destination.ForumList("1"))
        }
    }

    @Test
    fun `given group user and forum mode by default disabled when openUserDetail then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns
            MutableStateFlow(SettingsModel(openGroupsInForumModeByDefault = false))

        sut.openUserDetail(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.push(Destination.UserDetail("1"))
        }
    }

    @Test
    fun `when switch to classic mode then interactions are as expected`() {
        sut.switchUserDetailToClassicMode(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.replace(Destination.UserDetail("1"))
        }
    }

    @Test
    fun `when switch to forum mode then interactions are as expected`() {
        sut.switchUserDetailToForumMode(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.replace(Destination.ForumList("1"))
        }
    }

    @Test
    fun `when openEntryDetail then interactions are as expected`() {
        sut.openEntryDetail(entry = TimelineEntryModel(id = "0", content = ""))

        verify {
            navigationCoordinator.push(Destination.EntryDetail("0", swipeNavigationEnabled = false))
        }
    }

    @Test
    fun `when openEntryDetail with replaceTop then interactions are as expected`() {
        sut.openEntryDetail(
            entry = TimelineEntryModel(id = "0", content = ""),
            replaceTop = true,
        )

        verify {
            navigationCoordinator.replace(Destination.EntryDetail("0", swipeNavigationEnabled = false))
        }
    }

    @Test
    fun `when openEntryDetail with swipeNavigationEnabled then interactions are as expected`() {
        sut.openEntryDetail(
            entry = TimelineEntryModel(id = "0", content = ""),
            swipeNavigationEnabled = true,
        )

        verify {
            navigationCoordinator.push(Destination.EntryDetail("0", swipeNavigationEnabled = true))
        }
    }

    @Test
    fun `when openSettings then interactions are as expected`() {
        sut.openSettings()

        verify {
            navigationCoordinator.push(Destination.Settings)
        }
    }

    @Test
    fun `when openHashtag then interactions are as expected`() {
        sut.openHashtag(tag = "tag")

        verify {
            navigationCoordinator.push(Destination.HashTag("tag"))
        }
    }

    @Test
    fun `when openFollowers then interactions are as expected`() {
        sut.openFollowers(user = UserModel(id = "1"))

        verify {
            navigationCoordinator.push(Destination.UserList(type = UserListType.Follower.toInt(), userId = "1"))
        }
    }

    @Test
    fun `when openFollowing then interactions are as expected`() {
        sut.openFollowing(user = UserModel(id = "1"))

        verify {
            navigationCoordinator.push(Destination.UserList(type = UserListType.Following.toInt(), userId = "1"))
        }
    }

    @Test
    fun `when openFavorites then interactions are as expected`() {
        sut.openFavorites()

        verify {
            navigationCoordinator.push(Destination.Favorites(FavoritesType.Favorites.toInt()))
        }
    }

    @Test
    fun `when openBookmarks then interactions are as expected`() {
        sut.openBookmarks()

        verify {
            navigationCoordinator.push(Destination.Favorites(FavoritesType.Bookmarks.toInt()))
        }
    }

    @Test
    fun `when openFollowedHashtags then interactions are as expected`() {
        sut.openFollowedHashtags()

        verify {
            navigationCoordinator.push(Destination.FollowedHashtags)
        }
    }

    @Test
    fun `when openEntryUsersFavorite then interactions are as expected`() {
        sut.openEntryUsersFavorite(entryId = "1", count = 1)

        verify {
            navigationCoordinator.push(
                Destination.UserList(
                    type = UserListType.UsersFavorite.toInt(),
                    entryId = "1",
                    infoCount = 1,
                ),
            )
        }
    }

    @Test
    fun `when openEntryUsersReblog then interactions are as expected`() {
        sut.openEntryUsersReblog(entryId = "1", count = 1)

        verify {
            navigationCoordinator.push(
                Destination.UserList(
                    type = UserListType.UsersReblog.toInt(),
                    entryId = "1",
                    infoCount = 1,
                ),
            )
        }
    }

    @Test
    fun `when openComposer then interactions are as expected`() {
        sut.openComposer()

        verify {
            navigationCoordinator.push(Destination.Composer())
        }
    }

    @Test
    fun `given reply when openComposer then interactions are as expected`() {
        val entry = TimelineEntryModel(id = "0", content = "")
        val user = UserModel(id = "1")
        sut.openComposer(
            inReplyTo = entry,
            inReplyToUser = user,
        )

        verifySuspend {
            entryCache.put(entry.id, entry)
            userCache.put(user.id, user)
            navigationCoordinator.push(Destination.Composer(inReplyToId = entry.id))
        }
    }

    @Test
    fun `given reply when openEditUnpublished then interactions are as expected`() {
        val entry = TimelineEntryModel(id = "0", content = "")
        sut.openEditUnpublished(
            entry = entry,
            type = UnpublishedType.Scheduled,
        )

        verifySuspend {
            entryCache.put(entry.id, entry)
            navigationCoordinator.push(Destination.Composer(scheduledPostId = entry.id))
        }
    }

    @Test
    fun `when openSearch then interactions are as expected`() {
        sut.openSearch()

        verify {
            navigationCoordinator.push(Destination.Search)
        }
    }

    @Test
    fun `given reply when openThread then interactions are as expected`() {
        val entry = TimelineEntryModel(id = "0", content = "")
        sut.openThread(entry = entry)

        verifySuspend {
            entryCache.put(entry.id, entry)
            navigationCoordinator.push(Destination.Thread(entryId = entry.id, swipeNavigationEnabled = false))
        }
    }

    @Test
    fun `given reply when openThread with swipeNavigationEnabled then interactions are as expected`() {
        val entry = TimelineEntryModel(id = "0", content = "")
        sut.openThread(entry = entry, swipeNavigationEnabled = true)

        verifySuspend {
            entryCache.put(entry.id, entry)
            navigationCoordinator.push(Destination.Thread(entryId = entry.id, swipeNavigationEnabled = true))
        }
    }

    @Test
    fun `when openImageDetail then interactions are as expected`() {
        val urls = listOf("https://www.example.com")
        sut.openImageDetail(urls = urls, initialIndex = 0)

        verify {
            navigationCoordinator.push(Destination.ImageDetail(urls = urls, initialIndex = 0))
        }
    }

    @Test
    fun `when openBlockedAndMuted then interactions are as expected`() {
        sut.openBlockedAndMuted()

        verify {
            navigationCoordinator.push(Destination.ManageBlocks)
        }
    }

    @Test
    fun `when openCircles then interactions are as expected`() {
        sut.openCircles()

        verify {
            navigationCoordinator.push(Destination.Circles)
        }
    }

    @Test
    fun `when openCircleEditMembers then interactions are as expected`() {
        sut.openCircleEditMembers(groupId = "1")

        verify {
            navigationCoordinator.push(Destination.CircleMembers("1"))
        }
    }

    @Test
    fun `when openCircleTimeline then interactions are as expected`() {
        val circle = CircleModel(id = "id")
        sut.openCircleTimeline(circle)

        verifySuspend {
            circleCache.put(circle.id, circle)
            navigationCoordinator.push(Destination.CircleTimeline(circle.id))
        }
    }

    @Test
    fun `when openFollowRequests then interactions are as expected`() {
        sut.openFollowRequests()

        verify {
            navigationCoordinator.push(Destination.FollowRequests)
        }
    }

    @Test
    fun `when openEditProfile then interactions are as expected`() {
        sut.openEditProfile()

        verify {
            navigationCoordinator.push(Destination.EditProfile)
        }
    }

    @Test
    fun `when openNodeInfo then interactions are as expected`() {
        sut.openNodeInfo()

        verify {
            navigationCoordinator.push(Destination.NodeInfo)
        }
    }

    @Test
    fun `when openDirectMessages then interactions are as expected`() {
        sut.openDirectMessages()

        verify {
            navigationCoordinator.push(Destination.ConversationList)
        }
    }

    @Test
    fun `when openConversation then interactions are as expected`() {
        sut.openConversation(
            otherUser = UserModel(id = "1"),
            parentUri = "fake-url",
        )

        verify {
            navigationCoordinator.push(Destination.Conversation(otherUserId = "1", parentUri = "fake-url"))
        }
    }

    @Test
    fun `when openGallery then interactions are as expected`() {
        sut.openGallery()

        verify {
            navigationCoordinator.push(Destination.Gallery)
        }
    }

    @Test
    fun `when openAlbum then interactions are as expected`() {
        sut.openAlbum(name = "album")

        verify {
            navigationCoordinator.push(Destination.AlbumDetail("album"))
        }
    }

    @Test
    fun `when openUnpublished then interactions are as expected`() {
        sut.openUnpublished()

        verify {
            navigationCoordinator.push(Destination.Unpublished)
        }
    }

    @Test
    fun `when openCreateReport for user then interactions are as expected`() {
        sut.openCreateReport(user = UserModel(id = "1"))

        verify {
            navigationCoordinator.push(Destination.CreateReport("1"))
        }
    }

    @Test
    fun `when openCreateReport for entry then interactions are as expected`() {
        sut.openCreateReport(
            user = UserModel(id = "1"),
            entry = TimelineEntryModel(id = "2", content = ""),
        )

        verify {
            navigationCoordinator.push(Destination.CreateReport(userId = "1", entryId = "2"))
        }
    }

    @Test
    fun `when openUserFeedback then interactions are as expected`() {
        sut.openUserFeedback()

        verify {
            navigationCoordinator.push(Destination.UserFeedback)
        }
    }

    @Test
    fun `when openCalendar then interactions are as expected`() {
        sut.openCalendar()

        verify {
            navigationCoordinator.push(Destination.Calendar)
        }
    }

    @Test
    fun `when openEvent then interactions are as expected`() {
        val event =
            EventModel(
                id = "0",
                uri = "www.example.com",
                title = "test event",
                description = "",
                startTime = "",
            )
        sut.openEvent(event)

        verifySuspend {
            eventCache.put("0", event)
            navigationCoordinator.push(Destination.EventDetail(event.id))
        }
    }

    @Test
    fun `when openLicences then interactions are as expected`() {
        sut.openLicences()

        verify {
            navigationCoordinator.push(Destination.Licences)
        }
    }

    @Test
    fun `when openInternalWebView then interactions are as expected`() {
        val url = "https://www.google.com"
        sut.openInternalWebView(url)

        verify {
            navigationCoordinator.push(Destination.WebView(url))
        }
    }

    @Test
    fun `when openAnnouncements then interactions are as expected`() {
        sut.openAnnouncements()

        verify {
            navigationCoordinator.push(Destination.Announcements)
        }
    }

    @Test
    fun `when openAcknowledgements then interactions are as expected`() {
        sut.openAcknowledgements()

        verify {
            navigationCoordinator.push(Destination.Acknowledgements)
        }
    }

    @Test
    fun `when openShortcuts then interactions are as expected`() {
        sut.openShortcuts()

        verify {
            navigationCoordinator.push(Destination.ShortcutList)
        }
    }

    @Test
    fun `when openShortcut then interactions are as expected`() {
        sut.openShortcut("node")

        verify {
            navigationCoordinator.push(Destination.ShortcutTimeline("node"))
        }
    }
}
