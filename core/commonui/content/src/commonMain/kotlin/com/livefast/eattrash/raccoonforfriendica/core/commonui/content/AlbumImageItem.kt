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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel
import kotlin.math.roundToInt

@Composable
fun AlbumImageItem(
    attachment: AttachmentModel,
    modifier: Modifier = Modifier,
    autoload: Boolean = true,
    minHeight: Dp = 50.dp,
    maxHeight: Dp = Dp.Unspecified,
    onClick: (() -> Unit)? = null,
    options: List<Option> = emptyList(),
    onSelectOption: ((OptionId) -> Unit)? = null,
) {
    var showingAltText by remember { mutableStateOf(false) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }
    val additionalOffset = with(LocalDensity.current) { Spacing.xl.toPx().roundToInt() }
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }
    val iconModifier =
        Modifier
            .border(
                width = Dp.Hairline,
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
            autoload = autoload,
            contentDescription = attachment.description,
            quality = FilterQuality.Low,
            contentScale = ContentScale.FillWidth,
        )

        if (options.isNotEmpty()) {
            Box(
                modifier =
                Modifier.align(Alignment.TopEnd).padding(
                    bottom = Spacing.xxs,
                    end = Spacing.xs,
                ),
            ) {
                FilledIconButton(
                    modifier =
                    Modifier
                        .padding(5.dp)
                        .size(IconSize.s)
                        .onGloballyPositioned {
                            optionsOffset = it.positionInParent()
                        },
                    onClick = {
                        optionsMenuOpen = true
                    },
                ) {
                    Icon(
                        modifier = Modifier.padding(2.5.dp),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = LocalStrings.current.actionOpenOptions,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }

                CustomDropDown(
                    expanded = optionsMenuOpen,
                    onDismiss = {
                        optionsMenuOpen = false
                    },
                    offset =
                    with(LocalDensity.current) {
                        DpOffset(
                            x = optionsOffset.x.toDp(),
                            y = optionsOffset.y.toDp(),
                        )
                    },
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option.label)
                            },
                            onClick = {
                                optionsMenuOpen = false
                                onSelectOption?.invoke(option.id)
                            },
                        )
                    }
                }
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
                                        .padding(Spacing.s)
                                        .background(
                                            color =
                                            MaterialTheme.colorScheme
                                                .surfaceColorAtElevation(5.dp)
                                                .copy(alpha = 0.9f),
                                            shape = RoundedCornerShape(CornerSize.xl),
                                        ).clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                        ) {
                                            showingAltText = false
                                        }.padding(
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
                            contentDescription = LocalStrings.current.actionShowContentDescription,
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}
