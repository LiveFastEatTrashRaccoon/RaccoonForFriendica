package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TagModel

@Composable
fun FollowHashtagItem(
    hashtag: TagModel,
    modifier: Modifier = Modifier,
    onOpen: ((String) -> Unit)? = null,
    onToggleFollow: ((Boolean) -> Unit)? = null,
) {
    val fullColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onOpen?.invoke(hashtag.name)
                }.padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text =
                buildString {
                    append("#")
                    append(hashtag.name)
                },
            style = MaterialTheme.typography.bodyLarge,
            color = fullColor,
        )
        TwoStateButton(
            isProminent = hashtag.following != true,
            label = LocalStrings.current.actionUnfollow,
            prominentLabel = LocalStrings.current.actionFollow,
            pending = hashtag.followingPending,
            onValueChange = { notFollowing ->
                onToggleFollow?.invoke(!notFollowing)
            },
        )
    }
}
