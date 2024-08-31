package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentImage(
    url: String,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    altText: String? = null,
    minHeight: Dp = 50.dp,
    maxHeight: Dp = Dp.Unspecified,
    onClick: (() -> Unit)? = null,
) {
    var revealing by remember { mutableStateOf(!sensitive) }
    var showingAltText by remember { mutableStateOf(false) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }
    val additionalOffset = with(LocalDensity.current) { Spacing.xl.toPx().roundToInt() }

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

        Row(
            modifier =
                Modifier.align(Alignment.BottomEnd).padding(
                    bottom = Spacing.xxs,
                    end = Spacing.xs,
                ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            val iconModifier =
                Modifier
                    .size(IconSize.m)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape,
                    ).clip(CircleShape)
            if (!altText.isNullOrEmpty()) {
                Box {
                    if (showingAltText) {
                        Popup(
                            offset =
                                IntOffset(
                                    x = 0,
                                    y = popupOffset.y.roundToInt() - additionalOffset,
                                ),
                            onDismissRequest = {},
                            content = {
                                Box(
                                    modifier =
                                        Modifier
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                            ) {
                                                showingAltText = false
                                            }.padding(Spacing.s)
                                            .background(
                                                color =
                                                    MaterialTheme.colorScheme
                                                        .surfaceColorAtElevation(5.dp)
                                                        .copy(alpha = 0.9f),
                                                shape = RoundedCornerShape(CornerSize.xl),
                                            ).padding(
                                                vertical = Spacing.s,
                                                horizontal = Spacing.m,
                                            ),
                                ) {
                                    Text(
                                        text = altText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            },
                        )
                    }
                    IconButton(
                        modifier =
                            Modifier.onGloballyPositioned {
                                popupOffset = it.positionInParent()
                            },
                        onClick = {
                            showingAltText = !showingAltText
                        },
                    ) {
                        Icon(
                            modifier = iconModifier,
                            imageVector = Icons.Default.Abc,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
            if (sensitive) {
                IconButton(
                    onClick = {
                        revealing = !revealing
                    },
                ) {
                    Icon(
                        modifier = iconModifier,
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
    }
}
