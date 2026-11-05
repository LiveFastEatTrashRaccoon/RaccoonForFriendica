package com.livefast.eattrash.raccoonforfriendica.feature.settings.timelinetypes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
internal fun TimelineTypeItem(
    type: TimelineType,
    reorderEnabled: Boolean = false,
    reorderableScope: ReorderableCollectionItemScope? = null,
    options: List<Option> = emptyList(),
    onSelectOption: ((OptionId) -> Unit)? = null,
) {
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }
    Row(
        modifier =
        Modifier.fillMaxWidth()
            .then(
                null?.let<() -> Unit, Modifier> { Modifier.clickable(onClick = it) }
                    ?: Modifier.Companion,
            )
            .padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(IconSize.m),
            imageVector = type.toIcon(),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
        )
        Column(
            modifier = Modifier.weight(1f).padding(start = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = type.toReadableName(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
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
                        imageVector = LocalResources.current.moreVert,
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
        } else {
            Spacer(modifier = Modifier.minimumInteractiveComponentSize())
        }

        if (reorderEnabled) {
            Box(modifier = Modifier.minimumInteractiveComponentSize()) {
                Image(
                    modifier = Modifier.then(
                        if (reorderableScope != null) {
                            with(reorderableScope) {
                                Modifier.draggableHandle(enabled = reorderEnabled)
                            }
                        } else {
                            Modifier
                        },
                    ),
                    imageVector = LocalResources.current.dragHandle,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                )
            }
        }
    }
}
