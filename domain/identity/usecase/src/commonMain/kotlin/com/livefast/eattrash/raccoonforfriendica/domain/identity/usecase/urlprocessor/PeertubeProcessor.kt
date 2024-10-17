package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.UriHandlerConstants.DETAIL_FRAGMENT
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor.UriHandlerConstants.INSTANCE_FRAGMENT

internal interface PeertubeProcessor : UrlProcessor

internal class DefaultPeertubeProcessor(
    private val detailOpener: DetailOpener,
    private val fetchUser: FetchUserUseCase,
) : PeertubeProcessor {
    override suspend fun process(uri: String): Boolean {
        val group = REGEX.find(uri)?.groups ?: return false
        val (node, user) = group["instance"]?.value.orEmpty() to group["detail"]?.value.orEmpty()
        val remoteUser = fetchUser("$user@$node") ?: return false

        detailOpener.openUserDetail(remoteUser)
        return true
    }

    companion object {
        private val REGEX =
            Regex("https://(?<instance>$INSTANCE_FRAGMENT)/(video-channels|accounts)/(?<detail>$DETAIL_FRAGMENT)")
    }
}
