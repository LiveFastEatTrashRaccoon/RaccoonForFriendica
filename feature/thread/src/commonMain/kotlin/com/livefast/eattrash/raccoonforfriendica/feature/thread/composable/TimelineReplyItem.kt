package com.livefast.eattrash.raccoonforfriendica.feature.thread.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomDropDown
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentAttachments
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentBody
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentFooter
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentHeader
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.ContentTitle
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.Option
import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.OptionId
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.embeddedImageUrls
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original

@Composable
fun TimelineReplyItem(
    entry: TimelineEntryModel,
    modifier: Modifier = Modifier,
    actionsEnabled: Boolean = true,
    blurNsfw: Boolean = true,
    autoloadImages: Boolean = true,
    options: List<Option> = emptyList(),
    onOpenUrl: ((String) -> Unit)? = null,
    onClick: ((TimelineEntryModel) -> Unit)? = null,
    onOpenUser: ((UserModel) -> Unit)? = null,
    onReply: ((TimelineEntryModel) -> Unit)? = null,
    onReblog: ((TimelineEntryModel) -> Unit)? = null,
    onFavorite: ((TimelineEntryModel) -> Unit)? = null,
    onBookmark: ((TimelineEntryModel) -> Unit)? = null,
    onOpenImage: ((List<String>, Int, List<Int>) -> Unit)? = null,
    onOptionSelected: ((OptionId) -> Unit)? = null,
) {
    val entryToDisplay = entry.original
    val depthZeroBased = entry.depth - 1
    val themeRepository = remember { getThemeRepository() }
    val barWidth = 3.dp
    val barColor = themeRepository.getCommentBarColor(depthZeroBased)
    var barHeight by remember { mutableStateOf(0.dp) }
    val indentAmount = Spacing.s + (barWidth + Spacing.s) * depthZeroBased
    val localDensity = LocalDensity.current
    var optionsOffset by remember { mutableStateOf(Offset.Zero) }
    var optionsMenuOpen by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .clickable {
                    onClick?.invoke(entryToDisplay)
                },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            // comment bar
            Box(
                modifier =
                    Modifier
                        .padding(start = indentAmount)
                        .size(width = barWidth, height = barHeight)
                        .background(
                            color = barColor,
                            shape = RoundedCornerShape(barWidth / 2),
                        ),
            )

            // comment content
            Column(
                modifier =
                    Modifier.onGloballyPositioned {
                        barHeight =
                            with(localDensity) {
                                it.size
                                    .toSize()
                                    .height
                                    .toDp()
                            }
                    },
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ContentHeader(
                        modifier = Modifier.weight(1f),
                        user = entryToDisplay.creator,
                        autoloadImages = autoloadImages,
                        date = entryToDisplay.updated ?: entryToDisplay.created,
                        isEdited = entryToDisplay.updated != null,
                        onOpenUser = onOpenUser,
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
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }

                            CustomDropDown(
                                expanded = optionsMenuOpen,
                                onDismiss = {
                                    optionsMenuOpen = false
                                },
                                offset =
                                    with(localDensity) {
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

                val title = entryToDisplay.title
                if (!title.isNullOrBlank()) {
                    ContentTitle(
                        modifier = Modifier.fillMaxWidth(),
                        content = title,
                        onClick = { onClick?.invoke(entryToDisplay) },
                        onOpenUrl = onOpenUrl,
                    )
                }

                val body = entryToDisplay.content
                if (body.isNotBlank()) {
                    ContentBody(
                        modifier = Modifier.fillMaxWidth(),
                        content = entryToDisplay.content,
                        onClick = { onClick?.invoke(entryToDisplay) },
                        onOpenUrl = onOpenUrl,
                    )
                }

                ContentAttachments(
                    modifier =
                        Modifier.fillMaxWidth().padding(
                            top = Spacing.s,
                            bottom = Spacing.xxxs,
                            start = Spacing.s,
                            end = Spacing.s,
                        ),
                    attachments =
                        entryToDisplay.attachments.filter {
                            it.url !in entryToDisplay.embeddedImageUrls
                        },
                    blurNsfw = blurNsfw,
                    autoloadImages = autoloadImages,
                    sensitive = entryToDisplay.sensitive,
                    onOpenImage = onOpenImage,
                )

                if (actionsEnabled) {
                    ContentFooter(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.xxs),
                        favoriteCount = entryToDisplay.favoriteCount,
                        favorite = entryToDisplay.favorite,
                        favoriteLoading = entryToDisplay.favoriteLoading,
                        reblogCount = entryToDisplay.reblogCount,
                        reblogged = entryToDisplay.reblogged,
                        reblogLoading = entryToDisplay.reblogLoading,
                        bookmarked = entryToDisplay.bookmarked,
                        bookmarkLoading = entryToDisplay.bookmarkLoading,
                        replyCount = entryToDisplay.replyCount,
                        onReply = {
                            onReply?.invoke(entryToDisplay)
                        },
                        onReblog = {
                            onReblog?.invoke(entryToDisplay)
                        },
                        onFavorite = {
                            onFavorite?.invoke(entryToDisplay)
                        },
                        onBookmark = {
                            onBookmark?.invoke(entryToDisplay)
                        },
                    )
                }
            }
        }
    }
}
