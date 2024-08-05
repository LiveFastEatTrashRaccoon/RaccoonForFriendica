package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.livefast.eattrash.raccoonforfriendica.core.utils.di.getImageLoaderProvider

@Composable
fun CustomImage(
    modifier: Modifier = Modifier,
    url: String,
    blurred: Boolean = false,
    contentDescription: String? = null,
    quality: FilterQuality = FilterQuality.Medium,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    contentAlignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onLoading: @Composable (BoxScope.(Float?) -> Unit)? = null,
    onFailure: @Composable (BoxScope.(Throwable) -> Unit)? = null,
) {
    val imageLoaderProvider = remember { getImageLoaderProvider() }
    var painterState: AsyncImagePainter.State by remember {
        mutableStateOf(AsyncImagePainter.State.Empty)
    }
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        AsyncImage(
            modifier =
                Modifier
                    .fillMaxSize()
                    .blur(radius = if (blurred) 60.dp else 0.dp),
            model = url,
            contentDescription = contentDescription,
            filterQuality = quality,
            contentScale = contentScale,
            alignment = alignment,
            alpha = alpha,
            colorFilter = colorFilter,
            onState = {
                painterState = it
            },
            imageLoader = imageLoaderProvider.provideImageLoader(),
        )

        when (val state = painterState) {
            AsyncImagePainter.State.Empty -> Unit
            is AsyncImagePainter.State.Error -> {
                onFailure?.invoke(this, state.result.throwable)
            }

            is AsyncImagePainter.State.Loading -> {
                onLoading?.invoke(this, null)
            }

            else -> Unit
        }
    }
}
