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
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

internal class DefaultMediaRepository(private val provider: ServiceProvider) : MediaRepository {
    override suspend fun getBy(id: String): AttachmentModel? = try {
        provider.media.getBy(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun create(bytes: ByteArray, album: String, alt: String): AttachmentModel? = try {
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
            withTimeoutOrNull(5.seconds) {
                var url = res.url
                while (url.isEmpty()) {
                    delay(1.seconds)
                    val pollingRes = getBy(id)
                    if (pollingRes != null) {
                        url = pollingRes.url
                    }
                }
                url
            }.orEmpty()
        res.copy(url = url)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun update(id: String, alt: String): Boolean = try {
        val content =
            FormDataContent(
                parameters {
                    append("description", alt)
                },
            )
        provider.media.update(id = id, content = content)
        true
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        false
    }

    override suspend fun delete(id: String): Boolean = provider.media.delete(id = id)
}
