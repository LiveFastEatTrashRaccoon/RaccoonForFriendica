package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PhotoAlbumRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
internal class DefaultAlbumPhotoPaginationManager(
    private val albumRepository: PhotoAlbumRepository,
) : AlbumPhotoPaginationManager {
    private var specification: AlbumPhotoPaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore = false
    private val history = mutableListOf<AttachmentModel>()
    private val mutex = Mutex()

    override suspend fun reset(specification: AlbumPhotoPaginationSpecification) {
        this.specification = specification
        pageCursor = null
        mutex.withLock {
            history.clear()
        }
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
            }.orEmpty()

        return mutex.withLock {
            results
                .deduplicate()
                .updatePaginationData()
                .also { history.addAll(it) }
            // return a copy
            history.map { it }
        }
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
