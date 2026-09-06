package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(scope = AppScope::class, binding = binding<AlbumPhotoPaginationManager>())
@Inject
class DefaultAlbumPhotoPaginationManager(private val albumRepository: PhotoAlbumRepository) :
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
