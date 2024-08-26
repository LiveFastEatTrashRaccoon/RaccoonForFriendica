package com.livefast.eattrash.raccoonforfriendica.core.utils.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

@Stable
interface GalleryHelper {
    val supportsCustomPath: Boolean

    fun saveToGallery(
        bytes: ByteArray,
        name: String,
        additionalPathSegment: String? = null,
    ): Any?

    @Composable
    fun getImageFromGallery(result: (ByteArray) -> Unit)
}

/*
 * This is implemented as an extension because it is common to all platforms.
 */
suspend fun GalleryHelper.download(url: String): ByteArray =
    withContext(Dispatchers.IO) {
        println("Downloading $url")
        val engine = provideHttpClientEngine()
        val client = HttpClient(engine)
        client.prepareGet(url).execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.body()
            var result = byteArrayOf()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(4096)
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    result += bytes
                    println("Received ${result.size} bytes / ${httpResponse.contentLength()}")
                    yield()
                }
            }
            result
        }
    }
