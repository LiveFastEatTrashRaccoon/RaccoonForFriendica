package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage

@Composable
fun ContentImage(
    url: String,
    modifier: Modifier = Modifier,
    blurred: Boolean = false,
    minHeight: Dp = 200.dp,
    maxHeight: Dp = Dp.Unspecified,
    onClick: (() -> Unit)? = null,
) {
    CustomImage(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight)
                .clip(RoundedCornerShape(CornerSize.xl))
                .clickable {
                    onClick?.invoke()
                },
        url = url,
        quality = FilterQuality.Low,
        blurred = blurred,
        contentScale = ContentScale.FillWidth,
    )
}
