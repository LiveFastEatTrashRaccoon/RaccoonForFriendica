package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.DETAIL_FRAGMENT
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.INSTANCE_FRAGMENT

internal interface MastodonUserProcessor : UrlProcessor

internal class DefaultMastodonUserProcessor(
    private val detailOpener: DetailOpener,
    private val fetchUser: FetchUserUseCase,
) : MastodonUserProcessor {
    override suspend fun process(uri: String): Boolean {
        val group = REGEX.find(uri)?.groups ?: return false
        val (node, user) =
            group["instance"]?.value.orEmpty() to group["detail"]?.value.orEmpty().trimStart('@')
        val remoteUser = fetchUser("$user@$node") ?: return false

        detailOpener.openUserDetail(remoteUser)
        return true
    }

    companion object {
        private val REGEX =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/@(?<detail>$DETAIL_FRAGMENT)")
    }
}
