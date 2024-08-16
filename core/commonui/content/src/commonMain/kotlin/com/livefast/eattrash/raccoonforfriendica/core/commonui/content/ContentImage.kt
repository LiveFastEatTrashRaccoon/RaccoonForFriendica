package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage

@Composable
fun ContentImage(
    url: String,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    minHeight: Dp = 200.dp,
    maxHeight: Dp = Dp.Unspecified,
    onClick: (() -> Unit)? = null,
) {
    var revealing by remember { mutableStateOf(!sensitive) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight),
    ) {
        CustomImage(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(CornerSize.xl))
                    .clickable {
                        onClick?.invoke()
                    },
            url = url,
            quality = FilterQuality.Low,
            blurred = !revealing,
            contentScale = ContentScale.FillWidth,
        )

        if (sensitive) {
            Icon(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = Spacing.s, end = Spacing.s)
                        .size(IconSize.m)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape,
                        ).clip(CircleShape)
                        .clickable {
                            revealing = !revealing
                        }.padding(Spacing.xs),
                imageVector =
                    if (revealing) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
