package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.IconSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing

@Composable
fun TwoStateButton(
    label: String,
    modifier: Modifier = Modifier,
    prominentLabel: String = label,
    isProminent: Boolean = false,
    pending: Boolean = false,
    prominentColor: Color = ButtonDefaults.buttonColors().contentColor,
    onValueChange: ((Boolean) -> Unit)? = null,
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
                    color =
                    if (isProminent) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                )
            }
            Text(
                text =
                if (isProminent) {
                    prominentLabel
                } else {
                    label
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
    val buttonPadding =
        PaddingValues(horizontal = Spacing.l, vertical = 0.dp)
    if (isProminent) {
        Button(
            colors = ButtonDefaults.buttonColors().copy(contentColor = prominentColor),
            contentPadding = buttonPadding,
            onClick = {
                onValueChange?.invoke(!isProminent)
            },
        ) {
            buttonContent()
        }
    } else {
        OutlinedButton(
            contentPadding = buttonPadding,
            onClick = {
                onValueChange?.invoke(!isProminent)
            },
        ) {
            buttonContent()
        }
    }
}
