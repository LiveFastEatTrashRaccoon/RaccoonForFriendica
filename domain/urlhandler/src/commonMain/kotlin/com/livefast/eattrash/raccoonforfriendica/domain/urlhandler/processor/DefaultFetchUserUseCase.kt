package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultFetchUserUseCase(private val searchRepository: SearchRepository) : FetchUserUseCase {
    override suspend fun invoke(url: String): UserModel? =
        // wait at most SEARCH_TIMEOUT millis failing if the request takes longer
        withTimeoutOrNull(SEARCH_TIMEOUT) {
            searchRepository
                .search(
                    query = url,
                    resolve = true,
                    type = SearchResultType.Users,
                )
                .orEmpty()
                .mapNotNull { res ->
                    when (res) {
                        is ExploreItemModel.User -> res.user
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
