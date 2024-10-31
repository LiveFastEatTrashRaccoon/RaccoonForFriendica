package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import kotlin.math.roundToInt

@Composable
fun ContentImage(
    url: String,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    autoload: Boolean = true,
    altText: String? = null,
    blurHash: String? = null,
    originalWidth: Int = 0,
    originalHeight: Int = 0,
    minHeight: Dp = 50.dp,
    maxHeight: Dp = Dp.Unspecified,
    contentScale: ContentScale = ContentScale.FillWidth,
    onClick: (() -> Unit)? = null,
    centerComposable: (@Composable () -> Unit) = {},
) {
    var revealing by remember { mutableStateOf(!sensitive) }
    var showingAltText by remember { mutableStateOf(false) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }
    val additionalOffset = with(LocalDensity.current) { Spacing.xl.toPx().roundToInt() }
    var hasFinishedLoadingSuccessfully by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier.heightIn(min = minHeight, max = maxHeight),
    ) {
        if (!hasFinishedLoadingSuccessfully) {
            BlurredPreview(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerSize.xl))
                        .clickable {
                            onClick?.invoke()
                        },
                originalWidth = originalWidth,
                originalHeight = originalHeight,
                blurHash = blurHash,
                contentScale = contentScale,
            )
        }

        CustomImage(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(CornerSize.xl))
                    .then(
                        if (originalWidth > 0 && originalHeight > 0) {
                            Modifier.aspectRatio(originalWidth / originalHeight.toFloat())
                        } else {
                            Modifier.heightIn(150.dp)
                        },
                    ).clickable {
                        onClick?.invoke()
                    },
            url = url,
            autoload = autoload,
            contentDescription = altText,
            quality = FilterQuality.Low,
            blurred = !revealing,
            contentScale = contentScale,
            onSuccess = {
                hasFinishedLoadingSuccessfully = true
            },
        )

        Box(
            modifier = Modifier.align(Alignment.Center),
        ) {
            centerComposable()
        }

        Row(
            modifier =
                Modifier.align(Alignment.BottomEnd).padding(
                    bottom = Spacing.xxs,
                    end = Spacing.xs,
                ),
        ) {
            val iconModifier =
                Modifier
                    .border(
                        width = Dp.Hairline,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape,
                    ).padding(2.5.dp)
                    .clip(CircleShape)
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
