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
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun TwoStateFollowButton(
    following: Boolean,
    modifier: Modifier = Modifier,
    pending: Boolean = false,
    onClick: ((Boolean) -> Unit)? = null,
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
            Text(
                if (following) {
                    LocalStrings.current.actionUnfollow
                } else {
                    LocalStrings.current.actionFollow
                },
            )
        }
    }
    val buttonPadding =
        PaddingValues(horizontal = Spacing.l, vertical = 0.dp)
    val isProminent = !following
    if (isProminent) {
        Button(
            contentPadding = buttonPadding,
            onClick = {
                if (!pending) {
                    onClick?.invoke(!following)
                }
            },
        ) {
            buttonContent()
        }
    } else {
        OutlinedButton(
            contentPadding = buttonPadding,
            onClick = {
                if (!pending) {
                    onClick?.invoke(!following)
                }
            },
        ) {
            buttonContent()
        }
    }
}
