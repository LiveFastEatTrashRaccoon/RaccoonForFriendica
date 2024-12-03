package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Single

@Single
internal class DefaultFetchUserUseCase(
    private val searchRepository: SearchRepository,
) : FetchUserUseCase {
    override suspend fun invoke(url: String): UserModel? =
        // wait at most SEARCH_TIMEOUT_MILLIS failing if the request takes longer
        withTimeoutOrNull(SEARCH_TIMEOUT_MILLIS) {
            searchRepository
                .search(
                    query = url,
                    resolve = true,
                    type = SearchResultType.Users,
                ).orEmpty()
                .mapNotNull { res ->
                    when (res) {
                        is ExploreItemModel.User -> res.user
                        else -> null
                    }
                }.firstOrNull {
                    it.url == url
                }
        }

    companion object {
        private const val SEARCH_TIMEOUT_MILLIS = 1500L
    }
}
