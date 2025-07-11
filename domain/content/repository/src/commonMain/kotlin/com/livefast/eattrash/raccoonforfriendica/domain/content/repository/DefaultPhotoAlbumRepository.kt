package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters

internal class DefaultPhotoAlbumRepository(private val provider: ServiceProvider) : PhotoAlbumRepository {
    override suspend fun getAll(): List<MediaAlbumModel>? = runCatching {
        provider.photoAlbum.getAll().map { it.toModel() }
    }.getOrNull()

    override suspend fun update(oldName: String, newName: String): Boolean = runCatching {
        val data =
            FormDataContent(
                Parameters.build {
                    append("album", oldName)
                    append("album_new", newName)
                },
            )
        val res = provider.photoAlbum.update(data)
        res.result == "updated"
    }.getOrElse { false }

    override suspend fun delete(name: String): Boolean = runCatching {
        val data =
            FormDataContent(
                Parameters.build {
                    append("album", name)
                },
            )
        val res = provider.photoAlbum.delete(data)
        res.result == "deleted"
    }.getOrElse { false }

    override suspend fun getPhotos(album: String, pageCursor: String?, latestFirst: Boolean): List<AttachmentModel>? =
        runCatching {
            provider.photoAlbum
                .getPhotos(
                    album = album,
                    maxId = pageCursor,
                    latestFirst = latestFirst,
                    limit = DEFAULT_PAGE_SIZE,
                ).map { it.toModel() }
        }.getOrNull()

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
