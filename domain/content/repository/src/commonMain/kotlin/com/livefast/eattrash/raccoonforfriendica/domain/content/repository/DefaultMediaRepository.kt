package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

internal class DefaultMediaRepository(private val provider: ServiceProvider) : MediaRepository {
    override suspend fun getBy(id: String): AttachmentModel? = runCatching {
        provider.media.getBy(id).toModel()
    }.getOrNull()

    override suspend fun create(bytes: ByteArray, album: String, alt: String): AttachmentModel? = runCatching {
        val content =
            MultiPartFormDataContent(
                formData {
                    append(
                        key = "file",
                        value = bytes,
                        headers =
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/*")
                            append(HttpHeaders.ContentDisposition, "filename=image.jpeg")
                        },
                    )
                    append("description", alt)
                },
            )
        val res = provider.media.create(content = content).toModel()
        val id = res.id
        val url =
            withTimeoutOrNull(5000) {
                var url = res.url
                while (url.isEmpty()) {
                    delay(1000)
                    val pollingRes = getBy(id)
                    if (pollingRes != null) {
                        url = pollingRes.url
                    }
                }
                url
            }.orEmpty()
        res.copy(url = url)
    }.getOrNull()

    override suspend fun update(id: String, alt: String): Boolean = runCatching {
        val content =
            FormDataContent(
                parameters {
                    append("description", alt)
                },
            )
        provider.media.update(id = id, content = content)
        true
    }.getOrElse { false }

    override suspend fun delete(id: String): Boolean = provider.media.delete(id = id)
}
