package com.livefast.eattrash.raccoonforfriendica.feature.calendar.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentTitle
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.EventModel

@Composable
fun CalendarRow(
    event: EventModel,
    modifier: Modifier = Modifier,
    options: List<Option> = emptyList(),
    onOpenUrl: ((String, Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    onSelectOption: ((OptionId) -> Unit)? = null,
) {
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier =
        modifier
            .padding(bottom = Spacing.xxs)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick?.invoke()
            },
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ContentTitle(
                modifier = Modifier.weight(1f).padding(horizontal = Spacing.s),
                content = event.title,
                onOpenUrl = onOpenUrl?.let { block -> { url -> block(url, true) } },
                maxLines = 5,
                onClick = {
                    onClick?.invoke()
                },
            )

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
                                    onSelectOption?.invoke(option.id)
                                },
                            )
                        }
                    }
                }
            }
        }

        CalendarEventFooter(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.s),
            startDate = event.startTime,
            endDate = event.endTime,
            place = event.place,
        )
    }
}
