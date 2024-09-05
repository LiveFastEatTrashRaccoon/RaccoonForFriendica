package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

interface AlbumPhotoPaginationManager {
    val canFetchMore: Boolean

    suspend fun reset(specification: AlbumPhotoPaginationSpecification)

    suspend fun loadNextPage(): List<AttachmentModel>
}
