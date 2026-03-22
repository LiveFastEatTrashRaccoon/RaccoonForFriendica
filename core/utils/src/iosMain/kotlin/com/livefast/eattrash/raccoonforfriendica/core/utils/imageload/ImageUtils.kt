package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import coil3.decode.Decoder
import coil3.decode.SkiaImageDecoder
import coil3.network.NetworkFetcher
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.livefast.eattrash.raccoonforfriendica.core.utils.network.provideHttpClientEngine
import io.ktor.client.HttpClient
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

actual fun ByteArray.toComposeImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()

actual fun IntArray.toComposeImageBitmap(width: Int, height: Int): ImageBitmap {
    val bmp = Bitmap()
    val info = ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
    bmp.installPixels(info, map { it.toByte() }.toByteArray(), info.minRowBytes)

    // manually convert ARGB to RGBA because Skia's RGBA_8888 expects this specific byte order
    val pixels = ByteArray(width * height * 4)
    for (i in indices) {
        val argb = this[i]
        pixels[i * 4] = ((argb shr 16) and 0xff).toByte()     // R
        pixels[i * 4 + 1] = ((argb shr 8) and 0xff).toByte()  // G
        pixels[i * 4 + 2] = (argb and 0xff).toByte()         // B
        pixels[i * 4 + 3] = ((argb shr 24) and 0xff).toByte() // A
    }
    bmp.installPixels(info, pixels, info.minRowBytes)

    return bmp.asComposeImageBitmap()
}

actual fun getNativeDecoders(): List<Decoder.Factory> = buildList {
    add(SkiaImageDecoder.Factory())
}

actual fun getNativeFetchers(): List<NetworkFetcher.Factory> = buildList {
    val httpClient = HttpClient(provideHttpClientEngine())
    add(KtorNetworkFetcherFactory(httpClient))
}
