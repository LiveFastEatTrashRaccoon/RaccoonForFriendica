package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.prettifyHtml
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailDialog(entry: TimelineEntryModel, modifier: Modifier = Modifier, onClose: (() -> Unit)? = null) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val sourcePlatform = entry.sourcePlatform.orEmpty()
    val sourceProtocol = entry.sourceProtocol.orEmpty()
    val creationDate = entry.created.orEmpty()
    val updateDate = entry.updated.orEmpty()

    BasicAlertDialog(
        modifier = modifier.clip(RoundedCornerShape(CornerSize.xxl)),
        onDismissRequest = {
            onClose?.invoke()
        },
        content = {
            Column(
                modifier =
                Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                    .padding(Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Text(
                    text = LocalStrings.current.actionViewDetails,
                    style = MaterialTheme.typography.titleMedium,
                    color = fullColor,
                )

                LazyColumn(
                    modifier =
                    Modifier
                        .padding(vertical = Spacing.s, horizontal = Spacing.xs)
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        ) {
                            Text(
                                style = MaterialTheme.typography.labelMedium,
                                text = sourcePlatform,
                                color = fullColor,
                            )
                            if (sourceProtocol.isNotEmpty()) {
                                Text(
                                    style = MaterialTheme.typography.labelMedium,
                                    text =
                                    buildString {
                                        append("(")
                                        append(sourceProtocol)
                                        append(")")
                                    },
                                    color = fullColor,
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                style = MaterialTheme.typography.labelMedium,
                                text = entry.visibility.toReadableName(),
                                color = fullColor,
                            )
                            Icon(
                                modifier = Modifier.size(IconSize.s),
                                imageVector = entry.visibility.toIcon(),
                                contentDescription = entry.visibility.toReadableName(),
                                tint = fullColor,
                            )
                        }
                    }

                    entry.title?.takeIf { it.trim().isNotEmpty() }?.also {
                        item {
                            SelectionContainer {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = it.prettifyHtml(),
                                    style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                    color = fullColor,
                                )
                            }
                        }
                    }

                    entry.spoiler?.takeIf { it.trim().isNotEmpty() }?.also {
                        item {
                            SelectionContainer {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = it.prettifyHtml(),
                                    style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                    color = fullColor,
                                )
                            }
                        }
                    }

                    entry.content.takeIf { it.trim().isNotEmpty() }?.also {
                        item {
                            SelectionContainer {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = it.prettifyHtml(),
                                    style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                    color = fullColor,
                                )
                            }
                        }
                    }

                    if (entry.attachments.isNotEmpty()) {
                        item {
                            HorizontalDivider()
                            Column(
                                modifier = Modifier.padding(vertical = Spacing.xs),
                                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                            ) {
                                SelectionContainer {
                                    for (attachment in entry.attachments) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = attachment.url,
                                            style =
                                            MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                            ),
                                            color = fullColor,
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }

                    if (updateDate.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier.size(IconSize.m).padding(0.25.dp),
                                    imageVector = Icons.Default.Update,
                                    contentDescription = LocalStrings.current.updateDate,
                                    tint = fullColor,
                                )
                                Text(
                                    text = updateDate,
                                    style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                    color = fullColor,
                                )
                            }
                        }
                    }

                    if (creationDate.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    modifier = Modifier.size(IconSize.m).padding(2.dp),
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = LocalStrings.current.creationDate,
                                    tint = fullColor,
                                )
                                Text(
                                    text = creationDate,
                                    style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                    color = fullColor,
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.xxxs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        ) {
                            Icon(
                                modifier = Modifier.size(IconSize.m).padding(end = 3.5.dp),
                                imageVector = Icons.Default.ArrowCircleUp,
                                contentDescription =
                                LocalStrings.current.extendedSocialInfoFavorites(entry.favoriteCount),
                                tint = fullColor,
                            )
                            Text(
                                text = "${entry.favoriteCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = fullColor,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                modifier = Modifier.size(IconSize.m).padding(end = 3.5.dp),
                                imageVector = Icons.Default.ArrowCircleDown,
                                contentDescription =
                                LocalStrings.current.dislikesCount(entry.dislikesCount),
                                tint = fullColor,
                            )
                            Text(
                                text = "${entry.dislikesCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = fullColor,
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            onClose?.invoke()
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonClose)
                    }
                }
            }
        },
    )
}
