package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.di.SetupPreview
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.rememberBlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashParams
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.toComposeImageBitmap
import org.kodein.di.DI
import org.kodein.di.bindSingleton

@Composable
internal fun BlurredPreview(
    originalWidth: Int,
    originalHeight: Int,
    blurHash: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
) {
    val repository = rememberBlurHashRepository()

    if (originalWidth > 0 && originalHeight > 0) {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(blurHash) {
            if (blurHash != null) {
                // limit the size of the decoded blur hash to avoid memory issues
                // (blurred preview is intended to be decoded at low resolution and scaled up)
                val ratio = originalWidth.toFloat() / originalHeight
                val targetWidth = if (ratio > 1) 32 else (32 * ratio).toInt().coerceAtLeast(1)
                val targetHeight = if (ratio > 1) (32 / ratio).toInt().coerceAtLeast(1) else 32
                val params =
                    BlurHashParams(
                        hash = blurHash,
                        width = targetWidth,
                        height = targetHeight,
                    )
                imageBitmap = repository.get(params)
            }
        }

        Box(
            modifier = modifier.aspectRatio(originalWidth / originalHeight.toFloat()),
        ) {
            imageBitmap?.also { bmp ->
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = bmp,
                    contentDescription = null,
                    contentScale = contentScale,
                )
            }
        }
    }
}

@Composable
@Preview
private fun BlurredPreviewPreview() {
    RootDI.SetupPreview(
        DI.Module("BlurredPreviewPreviewModule") {
            bindSingleton {
                object : BlurHashRepository {
                    override suspend fun preload(params: BlurHashParams) = Unit

                    override suspend fun get(params: BlurHashParams): ImageBitmap = byteArrayOf(
                        -119, 80, 78, 71, 13, 10, 26, 10, 0, 0,
                        0, 13, 73, 72, 68, 82, 0, 0, 0, 2,
                        0, 0, 0, 2, 8, 2, 0, 0, 0, -3,
                        -44, -102, 115, 0, 0, 0, 22, 73, 68, 65,
                        84, 120, -100, 99, -8, -49, -64, -64, -64, -16,
                        -97, -111, -31, 63, -61, 127, 6, 6, 0, 28,
                        -8, 3, -2, -3, 114, -27, 20, 0, 0, 0,
                        0, 73, 69, 78, 68, -82, 66, 96, -126,
                    ).toComposeImageBitmap()
                }
            }
        },
    )
    BlurredPreview(
        originalWidth = 200,
        originalHeight = 200,
        blurHash = "fake",
    )
}
