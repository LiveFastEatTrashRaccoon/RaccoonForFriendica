package com.livefast.eattrash.raccoonforfriendica.feature.announcements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReactionModel

@Composable
internal fun ReactionItem(
    reaction: ReactionModel,
    modifier: Modifier = Modifier,
    onAdd: ((String) -> Unit)? = null,
    onRemove: ((String) -> Unit)? = null,
) {
    val shape = RoundedCornerShape(CornerSize.xxl)
    Row(
        modifier =
        modifier
            .clip(shape)
            .clickable {
                if (reaction.isMe) {
                    onRemove?.invoke(reaction.name)
                } else {
                    onAdd?.invoke(reaction.name)
                }
            }.then(
                if (reaction.isMe) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.66f),
                        shape = shape,
                    )
                } else {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                        shape = shape,
                    )
                },
            ).padding(
                vertical = Spacing.xs,
                horizontal = Spacing.s,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(IconSize.l),
            contentAlignment = Alignment.Center,
        ) {
            when {
                reaction.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(IconSize.s),
                        color =
                        if (reaction.isMe) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }

                !reaction.url.isNullOrEmpty() -> {
                    CustomImage(
                        modifier = Modifier.fillMaxSize(),
                        url = reaction.url.orEmpty(),
                    )
                }

                else -> {
                    Text(
                        text = reaction.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(end = Spacing.s),
            text = "${reaction.count}",
            style =
            MaterialTheme.typography.labelSmall.copy(
                fontWeight =
                if (reaction.isMe) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
            ),
        )
    }
}
