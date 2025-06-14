package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal class DefaultPhotoRepository(private val provider: ServiceProvider) : PhotoRepository {
    override suspend fun create(bytes: ByteArray, album: String, alt: String): AttachmentModel? = runCatching {
        val content =
            MultiPartFormDataContent(
                formData {
                    append(
                        key = "media",
                        value = bytes,
                        headers =
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/*")
                            append(HttpHeaders.ContentDisposition, "filename=image.jpeg")
                        },
                    )
                    append("album", album)
                    append("desc", alt)
                },
            )
        provider.photo.create(content = content).toModel()
    }.getOrNull()

    override suspend fun update(
        id: String,
        bytes: ByteArray?,
        album: String,
        newAlbum: String?,
        alt: String,
    ): Boolean = runCatching {
        val content =
            MultiPartFormDataContent(
                formData {
                    append("photo_id", id)
                    if (bytes != null) {
                        append(
                            key = "media",
                            value = bytes,
                            headers =
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/*")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=image.jpeg",
                                )
                            },
                        )
                    }
                    append("album", album)
                    if (newAlbum != null) {
                        append("album_new", newAlbum)
                    }
                    append("desc", alt)
                },
            )
        val res = provider.photo.update(content = content)
        res.result == "updated"
    }.getOrElse { false }

    override suspend fun delete(id: String): Boolean = runCatching {
        val content =
            MultiPartFormDataContent(
                formData {
                    append("photo_id", id)
                },
            )
        val res = provider.photo.delete(content)
        res.result == "deleted"
    }.getOrElse { false }
}
