package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds

@Single
internal class DefaultFetchEntryUseCase(private val searchRepository: SearchRepository) : FetchEntryUseCase {
    override suspend fun invoke(url: String): TimelineEntryModel? =
        // wait at most SEARCH_TIMEOUT millis failing if the request takes longer
        withTimeoutOrNull(SEARCH_TIMEOUT) {
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
        private val SEARCH_TIMEOUT = 1500.milliseconds
    }
}
