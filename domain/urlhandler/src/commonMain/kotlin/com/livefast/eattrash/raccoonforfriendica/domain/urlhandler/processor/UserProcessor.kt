package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

internal interface UserProcessor : UrlProcessor

@Single
internal class DefaultUserProcessor(
    private val mainRouter: MainRouter,
    private val fetchUser: FetchUserUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : UserProcessor {
    override suspend fun process(uri: String): Boolean {
        val remoteUser = fetchUser(uri) ?: return false
        withContext(dispatcher) {
            mainRouter.openUserDetail(remoteUser)
        }
        return true
    }
}
