package com.livefast.eattrash.raccoonforfriendica.feature.gallery.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.utils.datetime.getFormattedDate
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MediaAlbumModel

@Composable
internal fun MediaAlbumItem(
    album: MediaAlbumModel,
    modifier: Modifier = Modifier,
    options: List<Option> = emptyList(),
    onClick: (() -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }

    Row(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onClick?.invoke()
                }.padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceholderImage(
            title = album.name,
            size = IconSize.l,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = album.name,
                style = MaterialTheme.typography.titleMedium,
                color = fullColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text =
                    buildString {
                        val creationDate = album.created
                        if (creationDate != null) {
                            val formattedDate =
                                getFormattedDate(
                                    iso8601Timestamp = creationDate,
                                    format = "dd/MM/yy",
                                )
                            append(formattedDate)
                        }
                        if (isNotEmpty()) {
                            append(" • ")
                        }
                        append(album.items)
                        append(" ")
                        append(LocalStrings.current.items(album.items))
                    },
                style = MaterialTheme.typography.titleSmall,
                color = ancillaryColor,
            )
        }

        if (options.isNotEmpty()) {
            Box {
                IconButton(
                    modifier =
                        Modifier.onGloballyPositioned {
                            optionsOffset = it.positionInParent()
                        },
                    onClick = {
                        optionsMenuOpen = true
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(IconSize.s),
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
                                onOptionSelected?.invoke(option.id)
                            },
                        )
                    }
                }
            }
        }
    }
}
