package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing

@Composable
fun SectionSelector(
    currentSection: Int,
    onSelectSection: (Int) -> Unit,
    modifier: Modifier = Modifier,
    draggable: Boolean = true,
    scrollable: Boolean = false,
    titles: List<String> = emptyList(),
) {
    var isTowardsStart by remember { mutableStateOf(false) }
    val draggableState =
        remember {
            DraggableState { delta ->
                isTowardsStart = delta > 0
            }
        }
    val draggableModifier =
        if (draggable) {
            Modifier.draggable(
                state = draggableState,
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    if (isTowardsStart) {
                        onSelectSection((currentSection - 1).coerceAtLeast(0))
                    } else {
                        onSelectSection(
                            (currentSection + 1).coerceAtMost(
                                titles.lastIndex,
                            ),
                        )
                    }
                },
            )
        } else {
            Modifier
        }
    if (scrollable) {
        ScrollableTabRow(
            modifier = modifier,
            selectedTabIndex = currentSection,
            edgePadding = Spacing.xs,
            tabs = {
                titles.forEachIndexed { i, title ->
                    Tab(
                        modifier = draggableModifier,
                        selected = i == currentSection,
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                            )
                        },
                        onClick = {
                            onSelectSection(i)
                        },
                    )
                }
            },
        )
    } else {
        TabRow(
            modifier = modifier,
            selectedTabIndex = currentSection,
            tabs = {
                titles.forEachIndexed { i, title ->
                    Tab(
                        modifier = draggableModifier,
                        selected = i == currentSection,
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                            )
                        },
                        onClick = {
                            onSelectSection(i)
                        },
                    )
                }
            },
        )
    }
}
