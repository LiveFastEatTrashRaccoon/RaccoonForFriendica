package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType

interface SearchRepository {
    suspend fun search(
        query: String = "",
        type: SearchResultType = SearchResultType.Hashtags,
        pageCursor: String? = null,
    ): List<ExploreItemModel>?
}
