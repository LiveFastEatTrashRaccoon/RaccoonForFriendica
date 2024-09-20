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
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getBlurHashRepository
import com.livefast.eattrash.raccoonforfriendica.core.utils.imageload.BlurHashParams

@Composable
internal fun BlurredPreview(
    originalWidth: Int,
    originalHeight: Int,
    blurHash: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
) {
    val repository = remember { getBlurHashRepository() }

    if (originalWidth > 0 && originalHeight > 0) {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(blurHash) {
            if (blurHash != null) {
                val params =
                    BlurHashParams(
                        hash = blurHash,
                        width = originalWidth,
                        height = originalHeight,
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
