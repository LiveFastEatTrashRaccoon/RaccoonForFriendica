package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.ancillaryTextAlpha
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.CustomImage
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.PlaceholderImage
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isProminent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@Composable
fun AccountItem(
    account: AccountModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onRelationshipClicked: (() -> Unit)? = null,
) {
    val avatar = account.avatar.orEmpty()
    val avatarSize = IconSize.l
    val fullColor = MaterialTheme.colorScheme.onBackground
    val ancillaryColor = MaterialTheme.colorScheme.onBackground.copy(ancillaryTextAlpha)
    val relationshipStatus = account.relationshipStatus

    Row(
        modifier =
            modifier.clickable {
                onClick?.invoke()
            }.padding(Spacing.s),
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        if (avatar.isNotEmpty()) {
            CustomImage(
                modifier =
                    Modifier
                        .size(avatarSize)
                        .clip(RoundedCornerShape(avatarSize / 2)),
                url = avatar,
                quality = FilterQuality.Low,
                contentScale = ContentScale.FillBounds,
            )
        } else {
            PlaceholderImage(
                size = avatarSize,
                title = account.displayName ?: account.handle ?: "?",
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = account.displayName ?: account.username ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = fullColor,
            )
            Text(
                text = account.handle ?: account.username ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = ancillaryColor,
            )
        }

        if (relationshipStatus != null) {
            val buttonPadding =
                PaddingValues(horizontal = Spacing.l, vertical = 0.dp)
            if (relationshipStatus.isProminent()) {
                Button(
                    contentPadding = buttonPadding,
                    onClick = {
                        onRelationshipClicked?.invoke()
                    },
                ) {
                    Text(relationshipStatus.toReadableName())
                }
            } else {
                OutlinedButton(
                    contentPadding = buttonPadding,
                    onClick = {
                        onRelationshipClicked?.invoke()
                    },
                ) {
                    Text(relationshipStatus.toReadableName())
                }
            }
        }
    }
}
