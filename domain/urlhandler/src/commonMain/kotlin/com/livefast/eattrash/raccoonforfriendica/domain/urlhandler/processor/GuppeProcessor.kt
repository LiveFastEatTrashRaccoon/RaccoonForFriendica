package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UriHandlerConstants.DETAIL_FRAGMENT

internal interface GuppeProcessor : UrlProcessor

internal class DefaultGuppeProcessor(
    private val detailOpener: DetailOpener,
    private val fetchUser: FetchUserUseCase,
) : GuppeProcessor {
    override suspend fun process(uri: String): Boolean {
        val group = REGEX.find(uri)?.groups ?: return false
        val user = group["detail"]?.value.orEmpty()
        val remoteUser = fetchUser("$user@a.gup.pe") ?: return false

        detailOpener.openUserDetail(remoteUser)
        return true
    }

    companion object {
        private val REGEX = Regex("https://a\\.gup\\.pe/u/(?<detail>$DETAIL_FRAGMENT)")
    }
}
