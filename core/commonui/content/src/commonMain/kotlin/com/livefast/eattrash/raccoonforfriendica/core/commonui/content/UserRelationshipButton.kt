package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatus
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipStatusNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.getNextAction
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isProminent
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.toReadableName

@Composable
fun UserRelationshipButton(
    status: RelationshipStatus,
    modifier: Modifier = Modifier,
    pending: Boolean = false,
    onClick: ((RelationshipStatusNextAction) -> Unit)? = null,
) {
    val buttonContent = @Composable {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(Spacing.s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pending) {
                CircularProgressIndicator(
                    modifier = Modifier.size(IconSize.s),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(status.toReadableName())
        }
    }
    val buttonPadding =
        PaddingValues(horizontal = Spacing.l, vertical = 0.dp)
    val nextAction = status.getNextAction()

    if (status.isProminent()) {
        Button(
            contentPadding = buttonPadding,
            onClick = {
                onClick?.invoke(nextAction)
            },
        ) {
            buttonContent()
        }
    } else {
        OutlinedButton(
            contentPadding = buttonPadding,
            onClick = {
                onClick?.invoke(nextAction)
            },
        ) {
            buttonContent()
        }
    }
}
