package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.MainRouter

internal interface EntryProcessor : UrlProcessor

internal class DefaultEntryProcessor(private val mainRouter: MainRouter, private val fetchEntry: FetchEntryUseCase) :
    EntryProcessor {
    override suspend fun process(uri: String): Boolean {
        val remoteEntry = fetchEntry(uri) ?: return false
        mainRouter.openEntryDetail(remoteEntry)
        return true
    }
}
