package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModelWithReply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultSearchRepository(
    private val provider: ServiceProvider,
) : SearchRepository {
    override suspend fun search(
        query: String,
        type: SearchResultType,
        pageCursor: String?,
    ): List<ExploreItemModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    provider.search
                        .search(
                            query = query,
                            maxId = pageCursor,
                            limit = DEFAULT_PAGE_SIZE,
                            type = type.toDto(),
                        )
                when (type) {
                    SearchResultType.Entries -> response.statuses.map { ExploreItemModel.Entry(it.toModelWithReply()) }
                    SearchResultType.Hashtags -> response.hashtags.map { ExploreItemModel.HashTag(it.toModel()) }
                    SearchResultType.Users -> response.accounts.map { ExploreItemModel.User(it.toModel()) }
                }
            }.getOrElse { emptyList() }
        }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
