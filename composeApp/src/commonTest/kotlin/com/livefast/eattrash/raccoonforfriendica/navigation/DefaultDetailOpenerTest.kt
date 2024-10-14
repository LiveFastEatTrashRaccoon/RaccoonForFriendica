package com.livefast.eattrash.raccoonforfriendica.navigation

import com.livefast.eattrash.feature.userdetail.classic.UserDetailScreen
import com.livefast.eattrash.feature.userdetail.forum.ForumListScreen
import com.livefast.eattrash.raccoonforfriendica.core.navigation.NavigationCoordinator
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UnpublishedType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.LocalItemCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.feature.circles.detail.CircleDetailScreen
import com.livefast.eattrash.raccoonforfriendica.feature.circles.list.CirclesScreen
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.detail.ConversationScreen
import com.livefast.eattrash.raccoonforfriendica.feature.directmessages.list.DirectMessageListScreen
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class DefaultDetailOpenerTest {
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
    private val sut =
        DefaultDetailOpener(
            navigationCoordinator = navigationCoordinator,
            identityRepository = identityRepository,
            settingsRepository = settingsRepository,
            userCache = userCache,
            entryCache = entryCache,
        )

    @Test
    fun `given individual user when openUserDetail then interactions are as expected`() {
        sut.openUserDetail(user = UserModel(id = "1"))

        verify {
            navigationCoordinator.push(any<UserDetailScreen>())
        }
    }

    @Test
    fun `given group user when openUserDetail then interactions are as expected`() {
        sut.openUserDetail(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.push(any<ForumListScreen>())
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
            navigationCoordinator.push(any<ForumListScreen>())
        }
    }

    @Test
    fun `when switch to classic mode then interactions are as expected`() {
        sut.switchUserDetailToClassicMode(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.replace(any<UserDetailScreen>())
        }
    }

    @Test
    fun `when switch to forum mode then interactions are as expected`() {
        sut.switchUserDetailToForumMode(user = UserModel(id = "1", group = true))

        verify {
            navigationCoordinator.replace(any<ForumListScreen>())
        }
    }

    @Test
    fun `when openEntryDetail then interactions are as expected`() {
        sut.openEntryDetail(entry = TimelineEntryModel(id = "0", content = ""))

        verify {
            navigationCoordinator.push(any<EntryDetailScreen>())
        }
    }

    @Test
    fun `when openEntryDetail with replaceTop then interactions are as expected`() {
        sut.openEntryDetail(
            entry = TimelineEntryModel(id = "0", content = ""),
            replaceTop = true,
        )

        verify {
            navigationCoordinator.replace(any<EntryDetailScreen>())
        }
    }

    @Test
    fun `when openSettings then interactions are as expected`() {
        sut.openSettings()

        verify {
            navigationCoordinator.push(any<SettingsScreen>())
        }
    }

    @Test
    fun `when openHashtag then interactions are as expected`() {
        sut.openHashtag(tag = "tag")

        verify {
            navigationCoordinator.push(any<HashtagScreen>())
        }
    }

    @Test
    fun `when openFollowers then interactions are as expected`() {
        sut.openFollowers(user = UserModel(id = ""))

        verify {
            navigationCoordinator.push(any<UserListScreen>())
        }
    }

    @Test
    fun `when openFollowing then interactions are as expected`() {
        sut.openFollowing(user = UserModel(id = ""))

        verify {
            navigationCoordinator.push(any<UserListScreen>())
        }
    }

    @Test
    fun `when openFavorites then interactions are as expected`() {
        sut.openFavorites()

        verify {
            navigationCoordinator.push(any<FavoritesScreen>())
        }
    }

    @Test
    fun `when openBookmarks then interactions are as expected`() {
        sut.openBookmarks()

        verify {
            navigationCoordinator.push(any<FavoritesScreen>())
        }
    }

    @Test
    fun `when openFollowedHashtags then interactions are as expected`() {
        sut.openFollowedHashtags()

        verify {
            navigationCoordinator.push(any<FollowedHashtagsScreen>())
        }
    }

    @Test
    fun `when openEntryUsersFavorite then interactions are as expected`() {
        sut.openEntryUsersFavorite(entryId = "1", count = 1)

        verify {
            navigationCoordinator.push(any<UserListScreen>())
        }
    }

    @Test
    fun `when openEntryUsersReblog then interactions are as expected`() {
        sut.openEntryUsersReblog(entryId = "1", count = 1)

        verify {
            navigationCoordinator.push(any<UserListScreen>())
        }
    }

    @Test
    fun `when openComposer then interactions are as expected`() {
        sut.openComposer()

        verify {
            navigationCoordinator.push(any<ComposerScreen>())
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

        verify {
            entryCache.put(entry.id, entry)
            userCache.put(user.id, user)
            navigationCoordinator.push(any<ComposerScreen>())
        }
    }

    @Test
    fun `given reply when openEditUnpublished then interactions are as expected`() {
        val entry = TimelineEntryModel(id = "0", content = "")
        sut.openEditUnpublished(
            entry = entry,
            type = UnpublishedType.Scheduled,
        )

        verify {
            entryCache.put(entry.id, entry)
            navigationCoordinator.push(any<ComposerScreen>())
        }
    }

    @Test
    fun `when openSearch then interactions are as expected`() {
        sut.openSearch()

        verify {
            navigationCoordinator.push(any<SearchScreen>())
        }
    }

    @Test
    fun `given reply when openThread then interactions are as expected`() {
        val entry = TimelineEntryModel(id = "0", content = "")
        sut.openThread(entry = entry)

        verify {
            entryCache.put(entry.id, entry)
            navigationCoordinator.push(any<ThreadScreen>())
        }
    }

    @Test
    fun `when openImageDetail then interactions are as expected`() {
        val urls = listOf("https://www.example.com")
        sut.openImageDetail(urls = urls, initialIndex = 0)

        verify {
            navigationCoordinator.push(any<ImageDetailScreen>())
        }
    }

    @Test
    fun `given reply when openBlockedAndMuted then interactions are as expected`() {
        sut.openBlockedAndMuted()

        verify {
            navigationCoordinator.push(any<ManageBlocksScreen>())
        }
    }

    @Test
    fun `given reply when openCircles then interactions are as expected`() {
        sut.openCircles()

        verify {
            navigationCoordinator.push(any<CirclesScreen>())
        }
    }

    @Test
    fun `given reply when openCircle then interactions are as expected`() {
        sut.openCircle(groupId = "1")

        verify {
            navigationCoordinator.push(any<CircleDetailScreen>())
        }
    }

    @Test
    fun `given reply when openFollowRequests then interactions are as expected`() {
        sut.openFollowRequests()

        verify {
            navigationCoordinator.push(any<FollowRequestsScreen>())
        }
    }

    @Test
    fun `given reply openEditProfile then interactions are as expected`() {
        sut.openEditProfile()

        verify {
            navigationCoordinator.push(any<EditProfileScreen>())
        }
    }

    @Test
    fun `given reply openNodeInfo then interactions are as expected`() {
        sut.openNodeInfo()

        verify {
            navigationCoordinator.push(any<NodeInfoScreen>())
        }
    }

    @Test
    fun `given reply openDirectMessages then interactions are as expected`() {
        sut.openDirectMessages()

        verify {
            navigationCoordinator.push(any<DirectMessageListScreen>())
        }
    }

    @Test
    fun `given reply openConversation then interactions are as expected`() {
        sut.openConversation(
            otherUser = UserModel(id = "1"),
            parentUri = "",
        )

        verify {
            navigationCoordinator.push(any<ConversationScreen>())
        }
    }

    @Test
    fun `given reply openGallery then interactions are as expected`() {
        sut.openGallery()

        verify {
            navigationCoordinator.push(any<GalleryScreen>())
        }
    }

    @Test
    fun `given reply openAlbum then interactions are as expected`() {
        sut.openAlbum(name = "album")

        verify {
            navigationCoordinator.push(any<AlbumDetailScreen>())
        }
    }

    @Test
    fun `given reply openUnpublished then interactions are as expected`() {
        sut.openUnpublished()

        verify {
            navigationCoordinator.push(any<UnpublishedScreen>())
        }
    }

    @Test
    fun `given reply openCreateReport for user then interactions are as expected`() {
        sut.openCreateReport(user = UserModel(id = "1"))

        verify {
            navigationCoordinator.push(any<CreateReportScreen>())
        }
    }

    @Test
    fun `given reply openCreateReport for entry then interactions are as expected`() {
        sut.openCreateReport(
            user = UserModel(id = "1"),
            entry = TimelineEntryModel(id = "2", content = ""),
        )

        verify {
            navigationCoordinator.push(any<CreateReportScreen>())
        }
    }

    @Test
    fun `given reply openUserFeedback for user then interactions are as expected`() {
        sut.openUserFeedback()

        verify {
            navigationCoordinator.push(any<UserFeedbackScreen>())
        }
    }
}