package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.commonui.components.VideoPlayer
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

@Composable
fun ContentVideo(
    url: String,
    modifier: Modifier = Modifier,
    sensitive: Boolean = false,
    blurHash: String? = null,
    originalWidth: Int = 0,
    originalHeight: Int = 0,
    contentScale: ContentScale = ContentScale.FillWidth,
    onClick: (() -> Unit)? = null,
) {
    // TODO: add support for click ot open
    var revealing by remember { mutableStateOf(!sensitive) }
    var hasFinishedLoadingSuccessfully by remember { mutableStateOf(false) }
    val ratio =
        if (originalWidth != 0 && originalHeight != 0) {
            originalWidth / originalHeight.toFloat()
        } else {
            16 / 9f
        }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        if (!revealing) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(ratio),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.s),
                ) {
                    Text(
                        text = LocalStrings.current.messageVideoNsfw,
                        style = MaterialTheme.typography.bodyMedium,
                    )
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
            if (!hasFinishedLoadingSuccessfully) {
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(ratio),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            VideoPlayer(
                modifier =
                    Modifier.fillMaxWidth().aspectRatio(ratio),
                url = url,
                onPlaybackStarted = {
                    hasFinishedLoadingSuccessfully = true
                },
            )
            if (!hasFinishedLoadingSuccessfully) {
                CircularProgressIndicator(
                    modifier = Modifier.size(25.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
