package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository

internal class DefaultAlbumPhotoPaginationManager(
    private val albumRepository: PhotoAlbumRepository,
) : AlbumPhotoPaginationManager {
    private var specification: AlbumPhotoPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore = false
    private val history = mutableListOf<AttachmentModel>()

    override suspend fun reset(specification: AlbumPhotoPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<AttachmentModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is AlbumPhotoPaginationSpecification.Default ->
                    albumRepository.getPhotos(
                        album = specification.album,
                        pageCursor = pageCursor,
                        latestFirst = true,
                    )
            }.deduplicate().updatePaginationData()
        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<AttachmentModel>.updatePaginationData(): List<AttachmentModel> =
        apply {
            pageCursor = lastOrNull()?.id
            canFetchMore = isNotEmpty()
        }

    private fun List<AttachmentModel>.deduplicate(): List<AttachmentModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }.distinctBy { it.id }
}
