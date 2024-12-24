package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultEntryActionRepositoryTest {
    private val identityRepository =
        mock<IdentityRepository> {
            every { currentUser } returns MutableStateFlow(UserModel(id = USER_ID))
        }
    private val supportedFeatureRepository = mock<SupportedFeatureRepository>()
    private val sut =
        DefaultEntryActionRepository(
            identityRepository = identityRepository,
            supportedFeatureRepository = supportedFeatureRepository,
        )

    @Test
    fun `given post has URL when canShare then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
                url = "www.example.com",
            )

        val res = sut.canShare(entry)

        assertTrue(res)
    }

    @Test
    fun `given post does not have URL when canShare then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canShare(entry)

        assertFalse(res)
    }

    @Test
    fun `given logged when canReply then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canReply(entry)

        assertTrue(res)
    }

    @Test
    fun `given not logged when canReply then result is as expected`() {
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canReply(entry)

        assertFalse(res)
    }

    @Test
    fun `given logged when canFavorite then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canFavorite(entry)

        assertTrue(res)
    }

    @Test
    fun `given not logged when canFavorite then result is as expected`() {
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canFavorite(entry)

        assertFalse(res)
    }

    @Test
    fun `given not logged when canDislike then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canDislike(entry)

        assertFalse(res)
    }

    @Test
    fun `given logged and dislike not supported when canDislike then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canDislike(entry)

        assertFalse(res)
    }

    @Test
    fun `given logged and dislike supported when canDislike then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures(supportsDislike = true))
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canDislike(entry)

        assertTrue(res)
    }

    @Test
    fun `given not logged when canBookmark then result is as expected`() {
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canBookmark(entry)

        assertFalse(res)
    }

    @Test
    fun `given logged when canReblog then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canReblog(entry)

        assertTrue(res)
    }

    @Test
    fun `given not logged when canReblog then result is as expected`() {
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canReblog(entry)

        assertFalse(res)
    }

    @Test
    fun `given entry from other user when canEdit then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canEdit(entry)

        assertFalse(res)
    }

    @Test
    fun `given entry from current user when canEdit then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canEdit(entry)

        assertTrue(res)
    }

    @Test
    fun `given entry from other user when canDelete then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canDelete(entry)

        assertFalse(res)
    }

    @Test
    fun `given entry from current user when canDelete then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canDelete(entry)

        assertTrue(res)
    }

    @Test
    fun `given entry from other user when canReport then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canReport(entry)

        assertTrue(res)
    }

    @Test
    fun `given entry from current user when canReport then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canReport(entry)

        assertFalse(res)
    }

    @Test
    fun `given entry from other user when canMute then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canMute(entry)

        assertTrue(res)
    }

    @Test
    fun `given entry from current user when canMute then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canMute(entry)

        assertFalse(res)
    }

    @Test
    fun `given entry from other user when canTogglePin then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canTogglePin(entry)

        assertFalse(res)
    }

    @Test
    fun `given entry from current user when canTogglePin then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canTogglePin(entry)

        assertTrue(res)
    }

    @Test
    fun `given entry from other user when canBlock then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = sut.canBlock(entry)

        assertTrue(res)
    }

    @Test
    fun `given entry from current user when canBlock then result is as expected`() {
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canBlock(entry)

        assertFalse(res)
    }

    @Test
    fun `given share supported when canQuote then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures(supportsEntryShare = true))
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canQuote(entry)

        assertTrue(res)
    }

    @Test
    fun `given share unsupported when canQuote then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures())
        val entry =
            TimelineEntryModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = sut.canQuote(entry)

        assertFalse(res)
    }

    companion object {
        private const val USER_ID = "current-user-id"
        private const val OTHER_USER_ID = "other-user-id"
    }
}
