package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.VideoPlayer
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun ContentVideo(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    sensitive: Boolean = false,
    autoload: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    var revealing by remember { mutableStateOf(!sensitive && autoload) }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        if (!revealing) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    if (sensitive) {
                        Text(
                            text = LocalStrings.current.messageVideoNsfw,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            revealing = true
                        },
                    ) {
                        Text(text = LocalStrings.current.buttonLoad)
                    }
                }
            }
        } else {
            VideoPlayer(
                url = url,
                contentScale = contentScale,
            )
        }

        IconButton(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        start = Spacing.xs,
                    ),
            onClick = {
                onClick?.invoke()
            },
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = null,
            )
        }
    }
}
