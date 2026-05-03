package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultFetchEntryUseCase(private val searchRepository: SearchRepository) : FetchEntryUseCase {
    override suspend fun invoke(url: String): TimelineEntryModel? =
        // wait at most SEARCH_TIMEOUT_MILLIS failing if the request takes longer
        withTimeoutOrNull(SEARCH_TIMEOUT_MILLIS) {
            searchRepository
                .search(
                    query = url,
                    resolve = true,
                    type = SearchResultType.Entries,
                )
                .orEmpty()
                .mapNotNull { res ->
                    when (res) {
                        is ExploreItemModel.Entry -> res.entry
                        else -> null
                    }
                }.let { res ->
                    // eventual consistency check: prefer the one with the matching URL or fallback on the first one
                    res.firstOrNull { it.url == url } ?: res.firstOrNull()
                }
        }

    companion object {
        private const val SEARCH_TIMEOUT_MILLIS = 1500L
    }
}
