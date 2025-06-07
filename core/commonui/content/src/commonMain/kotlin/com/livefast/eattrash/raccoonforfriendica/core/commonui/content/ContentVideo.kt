package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.VideoPlayerPreview
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

@Composable
fun ContentVideo(
    url: String,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    autoload: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    var shouldBeRendered by remember { mutableStateOf(autoload) }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        if (shouldBeRendered) {
            VideoPlayerPreview(
                modifier =
                Modifier
                    .fillMaxSize()
                    .blur(radius = if (sensitive) 60.dp else 0.dp),
                url = url,
            )
            FilledIconButton(
                colors =
                IconButtonDefaults.filledIconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(
                        start = Spacing.xs,
                    ),
                onClick = {
                    onClick?.invoke()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = LocalStrings.current.actionOpenFullScreen,
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = {
                        shouldBeRendered = true
                    },
                ) {
                    Text(
                        text = LocalStrings.current.buttonLoad,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
