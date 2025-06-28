package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface EntryProcessor : UrlProcessor

internal class DefaultEntryProcessor(
    private val mainRouter: MainRouter,
    private val fetchEntry: FetchEntryUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : EntryProcessor {
    override suspend fun process(uri: String): Boolean {
        val remoteEntry = fetchEntry(uri) ?: return false
        withContext(dispatcher) {
            mainRouter.openEntryDetail(remoteEntry)
        }
        return true
    }
}
