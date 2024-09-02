package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultPhotoRepository(
    private val provider: ServiceProvider,
) : PhotoRepository {
    override suspend fun create(
        bytes: ByteArray,
        album: String,
        alt: String,
    ): AttachmentModel? =
        withContext(Dispatchers.IO) {
            runCatching {
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
            }
        }.getOrNull()

    override suspend fun update(
        id: String,
        bytes: ByteArray?,
        album: String,
        alt: String,
    ): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
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
                            append("desc", alt)
                        },
                    )
                provider.photo.update(content = content)
                true
            }
        }.getOrElse { false }

    override suspend fun delete(id: String): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val content =
                    MultiPartFormDataContent(
                        formData {
                            append("photo_id", id)
                        },
                    )
                provider.photo.delete(content)
                true
            }
        }.getOrElse { false }
}
