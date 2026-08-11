package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import org.koin.core.annotation.Factory

@Factory
internal class DefaultAlbumPhotoPaginationManager(private val albumRepository: PhotoAlbumRepository) :
    BasePaginationManager<AttachmentModel, AlbumPhotoPaginationSpecification>(
        idSelector = { it.id },
    ),
    AlbumPhotoPaginationManager {

    override suspend fun loadNextPage(): List<AttachmentModel> {
        val spec = currentSpecification ?: return emptyList()

        val results =
            when (spec) {
                is AlbumPhotoPaginationSpecification.Default ->
                    albumRepository.getPhotos(
                        album = spec.album,
                        pageCursor = currentPageCursor,
                        latestFirst = true,
                    )
            }

        return updateHistory(
            items = results,
        )
    }
}
