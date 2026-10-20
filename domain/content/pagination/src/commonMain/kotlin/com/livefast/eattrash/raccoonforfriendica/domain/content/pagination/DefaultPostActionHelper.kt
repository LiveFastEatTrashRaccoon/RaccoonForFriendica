package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostActions
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuotePermission
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultPostActionHelper(
    private val identityRepository: IdentityRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
) : PostActionHelper {
    private val currentUserId: String? get() = identityRepository.currentUser.value?.id
    private val isLogged: Boolean get() = currentUserId != null

    override fun PostModel.withActions(): PostModel = copy(
        actionPermissions = PostActions(
            canShare = !url.isNullOrBlank(),
            canReply = isLogged,
            canFavorite = isLogged,
            canDislike = isLogged && supportedFeatureRepository.features.value.supportsDislike,
            canBookmark = isLogged,
            canReblog = isFromOtherUser,
            canEdit = isFromCurrentUser,
            canDelete = isFromCurrentUser,
            canReport = isFromOtherUser,
            canMute = isFromOtherUser,
            canTogglePin = isFromCurrentUser,
            canBlock = isFromOtherUser,
            canQuote = when {
                // Friendica
                supportedFeatureRepository.features.value.supportsEntryShare -> true

                // Mastodon
                quotePermission in listOf(QuotePermission.AutomaticallyApprove, QuotePermission.ManualApprove) -> true

                else -> false
            },
        ),
        reblog = reblog?.withActions(),
    )

    private val PostModel.isFromCurrentUser: Boolean
        get() = isLogged && creator?.id == currentUserId

    private val PostModel.isFromOtherUser: Boolean
        get() = isLogged && creator?.id != currentUserId
}
