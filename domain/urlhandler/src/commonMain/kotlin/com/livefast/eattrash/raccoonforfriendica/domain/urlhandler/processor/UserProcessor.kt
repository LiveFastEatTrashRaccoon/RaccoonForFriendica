package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter

internal interface UserProcessor : UrlProcessor

internal class DefaultUserProcessor(private val mainRouter: MainRouter, private val fetchUser: FetchUserUseCase) :
    UserProcessor {
    override suspend fun process(uri: String): Boolean {
        val remoteUser = fetchUser(uri) ?: return false
        mainRouter.openUserDetail(remoteUser)
        return true
    }
}
