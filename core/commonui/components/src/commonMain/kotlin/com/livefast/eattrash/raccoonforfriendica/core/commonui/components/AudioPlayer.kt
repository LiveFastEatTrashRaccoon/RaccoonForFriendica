package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import chaintech.videoplayer.model.AudioFile
import chaintech.videoplayer.model.AudioPlayerConfig
import chaintech.videoplayer.ui.audio.AlbumArt
import chaintech.videoplayer.ui.audio.AudioPlayerComposable
import chaintech.videoplayer.ui.audio.TimeDetails
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.resources.di.getCoreResources

@Composable
fun AudioPlayer(
    urls: List<String>,
    titles: List<String>,
    modifier: Modifier = Modifier,
    autoplay: Boolean = false,
) {
    val resources = remember { getCoreResources() }
    val config = resources.getAudioPlayerConfig()
    var isInitial by remember { mutableStateOf(true) }

    if (!autoplay && isInitial) {
        FakeAudioPlayerComposable(
            modifier = modifier,
            audioPlayerConfig = config,
            onPlay = {
                isInitial = false
            },
        )
    } else {
        AudioPlayerComposable(
            modifier = modifier,
            audioPlayerConfig = config,
            audios =
                urls.mapIndexed { idx, url ->
                    AudioFile(
                        audioUrl = url,
                        audioTitle = titles.getOrNull(idx).orEmpty(),
                    )
                },
        )
    }
}

/**
 * Create a "fake" player with the same aspect as the original one, when tapping on the play
 * button it calls the onPlay callback which can be used to display the real player.
 *
 * It also hides all the controls except "play" in order to make it easier to skip with talkback.
 */
@Composable
private fun FakeAudioPlayerComposable(
    modifier: Modifier = Modifier,
    audioPlayerConfig: AudioPlayerConfig,
    onPlay: () -> Unit,
) {
    Box(
        modifier = modifier.background(audioPlayerConfig.backgroundColor),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = audioPlayerConfig.controlsBottomPadding),
                verticalArrangement = Arrangement.spacedBy(30.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .padding(horizontal = 25.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.weight(0.25f))
                    AlbumArt(
                        audioPlayerConfig = audioPlayerConfig,
                        thumbnailUrl = "",
                    )
                    Spacer(modifier = Modifier.weight(0.05f))
                }

                if (audioPlayerConfig.isControlsVisible) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            Slider(
                                enabled = false,
                                modifier = Modifier.fillMaxWidth().height(25.dp),
                                value = 0f,
                                onValueChange = {},
                                colors =
                                    SliderDefaults.colors(
                                        thumbColor = audioPlayerConfig.seekBarThumbColor,
                                        inactiveTrackColor = audioPlayerConfig.seekBarInactiveTrackColor,
                                        activeTrackColor = audioPlayerConfig.seekBarActiveTrackColor,
                                    ),
                            )

                            TimeDetails(
                                audioPlayerConfig = audioPlayerConfig,
                                currentTime = 0,
                                totalTime = 0,
                            )
                        }
                    }

                    val controlPanelHeight =
                        max(
                            audioPlayerConfig.pauseResumeIconSize,
                            audioPlayerConfig.previousNextIconSize,
                        )

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(controlPanelHeight),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        IconButton(
                            onClick = onPlay,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = LocalStrings.current.actionPlay,
                                tint = audioPlayerConfig.iconsTintColor,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}
