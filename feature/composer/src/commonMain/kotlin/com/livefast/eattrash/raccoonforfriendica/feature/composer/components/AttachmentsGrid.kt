package com.livefast.eattrash.raccoonforfriendica.feature.composer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AttachmentModel

@Composable
internal fun AttachmentsGrid(
    attachments: List<AttachmentModel>,
    onDelete: ((AttachmentModel) -> Unit)? = null,
    onEditDescription: ((AttachmentModel) -> Unit)? = null,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        items(
            items = attachments,
            key = { it.id },
        ) { attachment ->
            val closeIconSize = IconSize.s
            Box(
                modifier =
                    Modifier
                        .padding(top = closeIconSize / 2)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(CornerSize.l),
                        ).padding(Spacing.xxxs),
            ) {
                if (!attachment.loading) {
                    Icon(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .offset {
                                    IntOffset(
                                        x = (closeIconSize / 2).roundToPx(),
                                        y = (-closeIconSize / 2).roundToPx(),
                                    )
                                }.size(closeIconSize)
                                .background(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = CircleShape,
                                ).clickable {
                                    onDelete?.invoke(attachment)
                                }.padding(Spacing.xxxs),
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background,
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .padding(
                                top = Spacing.m,
                                bottom = Spacing.s,
                                start = Spacing.xs,
                                end = Spacing.xs,
                            ).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    if (attachment.loading) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f),
                        ) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier
                                        .size(IconSize.l)
                                        .align(Alignment.Center),
                            )
                        }
                    } else {
                        CustomImage(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16 / 9f)
                                    .clip(RoundedCornerShape(CornerSize.l)),
                            url = attachment.url,
                            contentScale = ContentScale.Fit,
                        )
                    }
                    val isAltPlaceholder = attachment.description.isNullOrBlank()
                    Row(
                        modifier =
                            Modifier
                                .clickable {
                                    onEditDescription?.invoke(attachment)
                                }.padding(horizontal = Spacing.xxs),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text =
                                when {
                                    attachment.loading -> ""
                                    isAltPlaceholder -> {
                                        LocalStrings.current.pictureDescriptionPlaceholder
                                    }

                                    else -> {
                                        attachment.description.orEmpty()
                                    }
                                },
                            style = MaterialTheme.typography.labelLarge,
                            color =
                                if (isAltPlaceholder) {
                                    MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                },
                        )
                        if (!attachment.loading) {
                            Icon(
                                modifier = Modifier.size(IconSize.m),
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}
