package com.livefast.eattrash.raccoonforfriendica.feature.announcements.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ReactionModel

@Composable
internal fun AnnouncementReactions(
    reactions: List<ReactionModel>,
    modifier: Modifier = Modifier,
    onAddNew: (() -> Unit)? = null,
    onAdd: ((String) -> Unit)? = null,
    onRemove: ((String) -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            items(reactions) { reaction ->
                ReactionItem(
                    reaction = reaction,
                    onAdd = onAdd,
                    onRemove = onRemove,
                )
            }
        }
        IconButton(
            onClick = {
                onAddNew?.invoke()
            },
        ) {
            Icon(
                modifier = Modifier.size(IconSize.l),
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = LocalStrings.current.actionAddReaction,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
