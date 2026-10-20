package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PostProcessor : UrlProcessor

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultPostProcessor(
    private val mainRouter: MainRouter,
    private val fetchPost: FetchPostUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : PostProcessor {
    override suspend fun process(uri: String): Boolean {
        val remotePost = fetchPost(uri) ?: return false
        withContext(dispatcher) {
            mainRouter.openPostDetail(remotePost)
        }
        return true
    }
}
