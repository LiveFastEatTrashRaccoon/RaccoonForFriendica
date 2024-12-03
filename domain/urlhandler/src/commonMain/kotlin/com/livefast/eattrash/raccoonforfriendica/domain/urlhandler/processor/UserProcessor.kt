package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import org.koin.core.annotation.Single

internal interface UserProcessor : UrlProcessor

@Single
internal class DefaultUserProcessor(
    private val detailOpener: DetailOpener,
    private val fetchUser: FetchUserUseCase,
) : UserProcessor {
    override suspend fun process(uri: String): Boolean {
        val remoteUser = fetchUser(uri) ?: return false
        detailOpener.openUserDetail(remoteUser)
        return true
    }
}
