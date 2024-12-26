package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import chaintech.videoplayer.model.AudioFile
import chaintech.videoplayer.ui.audio.AudioPlayerComposable
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources

@Composable
fun AudioPlayer(
    urls: List<String>,
    titles: List<String>,
    modifier: Modifier = Modifier,
) {
    val resources = remember { getCoreResources() }
    val config = resources.getAudioPlayerConfig()
    AudioPlayerComposable(
        modifier = modifier,
        audios =
            urls.mapIndexed { i, u ->
                AudioFile(
                    audioUrl = u,
                    audioTitle = titles.getOrNull(i).orEmpty(),
                )
            },
        audioPlayerConfig = config,
    )
}
