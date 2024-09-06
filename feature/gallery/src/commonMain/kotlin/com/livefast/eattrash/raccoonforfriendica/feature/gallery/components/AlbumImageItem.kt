package com.livefast.eattrash.raccoonforfriendica.feature.gallery.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
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
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import kotlin.math.roundToInt

@Composable
internal fun AlbumImageItem(
    attachment: AttachmentModel,
    modifier: Modifier = Modifier,
    minHeight: Dp = 50.dp,
    maxHeight: Dp = Dp.Unspecified,
    onDelete: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    var showingAltText by remember { mutableStateOf(false) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }
    val additionalOffset = with(LocalDensity.current) { Spacing.xl.toPx().roundToInt() }
    val iconModifier =
        Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape,
            ).padding(2.5.dp)
            .clip(CircleShape)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight),
    ) {
        CustomImage(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerSize.xl))
                    .clickable {
                        onClick?.invoke()
                    },
            url = attachment.thumbnail ?: attachment.url,
            quality = FilterQuality.Low,
            contentScale = ContentScale.FillWidth,
        )

        Row(
            modifier =
                Modifier.align(Alignment.TopEnd).padding(
                    bottom = Spacing.xxs,
                    end = Spacing.xs,
                ),
        ) {
            FilledIconButton(
                modifier = Modifier.padding(5.dp).size(IconSize.s),
                onClick = {
                    onEdit?.invoke()
                },
            ) {
                Icon(
                    modifier = Modifier.padding(2.5.dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                )
            }
            FilledIconButton(
                modifier = Modifier.padding(5.dp).size(IconSize.s),
                onClick = {
                    onDelete?.invoke()
                },
            ) {
                Icon(
                    modifier = Modifier.padding(2.5.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        }

        if (attachment.loading) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(3.5f),
            ) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .size(IconSize.l)
                            .align(Alignment.Center),
                )
            }
        }

        Row(
            modifier =
                Modifier.align(Alignment.BottomEnd).padding(
                    bottom = Spacing.xxs,
                    end = Spacing.xs,
                ),
        ) {
            if (!attachment.description.isNullOrEmpty()) {
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
                                        text = attachment.description.orEmpty(),
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
        }
    }
}
