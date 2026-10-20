package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuotePermission
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

class DefaultPostActionHelperTest {
    private val identityRepository =
        mock<IdentityRepository> {
            every { currentUser } returns MutableStateFlow(UserModel(id = USER_ID))
        }
    private val supportedFeatureRepository = mock<SupportedFeatureRepository>()
    private val sut =
        DefaultPostActionHelper(
            identityRepository = identityRepository,
            supportedFeatureRepository = supportedFeatureRepository,
        )

    @Test
    fun `given post has URL when canShare then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
                url = "www.example.com",
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canShare)
    }

    @Test
    fun `given post does not have URL when canShare then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canShare)
    }

    @Test
    fun `given logged when canReply then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canReply)
    }

    @Test
    fun `given not logged when canReply then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canReply)
    }

    @Test
    fun `given logged when canFavorite then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canFavorite)
    }

    @Test
    fun `given not logged when canFavorite then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canFavorite)
    }

    @Test
    fun `given not logged when canDislike then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canDislike)
    }

    @Test
    fun `given logged and dislike not supported when canDislike then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canDislike)
    }

    @Test
    fun `given logged and dislike supported when canDislike then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures(supportsDislike = true))
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canDislike)
    }

    @Test
    fun `given not logged when canBookmark then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canBookmark)
    }

    @Test
    fun `given logged when canReblog then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canReblog)
    }

    @Test
    fun `given not logged when canReblog then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        every { identityRepository.currentUser } returns MutableStateFlow(null)
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canReblog)
    }

    @Test
    fun `given post from other user when canEdit then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canEdit)
    }

    @Test
    fun `given post from current user when canEdit then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canEdit)
    }

    @Test
    fun `given post from other user when canDelete then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canDelete)
    }

    @Test
    fun `given post from current user when canDelete then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canDelete)
    }

    @Test
    fun `given post from other user when canReport then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canReport)
    }

    @Test
    fun `given post from current user when canReport then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canReport)
    }

    @Test
    fun `given post from other user when canMute then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canMute)
    }

    @Test
    fun `given post from current user when canMute then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canMute)
    }

    @Test
    fun `given post from other user when canTogglePin then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canTogglePin)
    }

    @Test
    fun `given post from current user when canTogglePin then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canTogglePin)
    }

    @Test
    fun `given post from other user when canBlock then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = OTHER_USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canBlock)
    }

    @Test
    fun `given post from current user when canBlock then result is as expected`() {
        every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canBlock)
    }

    @Test
    fun `given share supported when canQuote then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures(supportsEntryShare = true))
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canQuote)
    }

    @Test
    fun `given share unsupported and allow policy when canQuote then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
                quotePermission = QuotePermission.AutomaticallyApprove,
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertTrue(res.canQuote)
    }

    @Test
    fun `given share unsupported and deny policy when canQuote then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
                quotePermission = QuotePermission.Unauthorized,
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canQuote)
    }

    @Test
    fun `given share unsupported and unknown policy when canQuote then result is as expected`() {
        every { supportedFeatureRepository.features } returns
            MutableStateFlow(NodeFeatures())
        val post =
            PostModel(
                id = "0",
                content = "",
                creator = UserModel(id = USER_ID),
            )

        val res = with(sut) { post.withActions() }.actionPermissions

        assertFalse(res.canQuote)
    }

    companion object {
        private const val USER_ID = "current-user-id"
        private const val OTHER_USER_ID = "other-user-id"
    }
}
