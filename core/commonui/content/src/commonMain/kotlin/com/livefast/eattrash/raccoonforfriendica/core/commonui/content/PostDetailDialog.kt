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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
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
import com.livefast.eattrash.raccoonforfriendica.core.resources.LocalResources
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toIcon
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@Composable
fun PostDetailDialog(post: PostModel, modifier: Modifier = Modifier, onClose: (() -> Unit)? = null) {
    val fullColor = MaterialTheme.colorScheme.onBackground
    val sourcePlatform = post.sourcePlatform.orEmpty()
    val sourceProtocol = post.sourceProtocol.orEmpty()
    val creationDate = post.created.orEmpty()
    val updateDate = post.updated.orEmpty()

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
                                text = post.visibility.toReadableName(),
                                color = fullColor,
                            )
                            Icon(
                                modifier = Modifier.size(IconSize.s),
                                imageVector = post.visibility.toIcon(LocalResources.current),
                                contentDescription = post.visibility.toReadableName(),
                                tint = fullColor,
                            )
                        }
                    }

                    post.title?.takeIf { it.trim().isNotEmpty() }?.also {
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

                    post.spoiler?.takeIf { it.trim().isNotEmpty() }?.also {
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

                    post.content.takeIf { it.trim().isNotEmpty() }?.also {
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

                    if (post.attachments.isNotEmpty()) {
                        item {
                            HorizontalDivider()
                            Column(
                                modifier = Modifier.padding(vertical = Spacing.xs),
                                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                            ) {
                                SelectionContainer {
                                    for (attachment in post.attachments) {
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
                                    imageVector = LocalResources.current.update,
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
                                    imageVector = LocalResources.current.schedule,
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
                                imageVector = LocalResources.current.arrowCircleUp,
                                contentDescription =
                                LocalStrings.current.extendedSocialInfoFavorites(post.favoriteCount),
                                tint = fullColor,
                            )
                            Text(
                                text = "${post.favoriteCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = fullColor,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                modifier = Modifier.size(IconSize.m).padding(end = 3.5.dp),
                                imageVector = LocalResources.current.arrowCircleDown,
                                contentDescription =
                                LocalStrings.current.dislikesCount(post.dislikesCount),
                                tint = fullColor,
                            )
                            Text(
                                text = "${post.dislikesCount}",
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
