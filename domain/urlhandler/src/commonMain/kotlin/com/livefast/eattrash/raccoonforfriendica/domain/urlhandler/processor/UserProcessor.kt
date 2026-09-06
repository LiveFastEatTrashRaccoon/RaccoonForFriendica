package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserProcessor : UrlProcessor

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultUserProcessor(
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
