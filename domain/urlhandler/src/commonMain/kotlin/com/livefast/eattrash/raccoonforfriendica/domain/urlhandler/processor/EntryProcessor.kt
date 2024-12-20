package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener

internal interface EntryProcessor : UrlProcessor

internal class DefaultEntryProcessor(
    private val detailOpener: DetailOpener,
    private val fetchEntry: FetchEntryUseCase,
) : EntryProcessor {
    override suspend fun process(uri: String): Boolean {
        val remoteEntry = fetchEntry(uri) ?: return false
        detailOpener.openEntryDetail(remoteEntry)
        return true
    }
}
