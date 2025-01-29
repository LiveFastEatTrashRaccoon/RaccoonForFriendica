package com.livefast.eattrash.raccoonforfriendica.feature.manageblocks.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
internal fun GenericListItem(
    title: String,
    subtitle: String? = null,
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
                .then(
                    if (onClick != null) {
                        Modifier.clickable {
                            onClick()
                        }
                    } else {
                        Modifier
                    },
                ).padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(start = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = fullColor,
            )
            if (subtitle != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = ancillaryColor,
                )
            }
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
