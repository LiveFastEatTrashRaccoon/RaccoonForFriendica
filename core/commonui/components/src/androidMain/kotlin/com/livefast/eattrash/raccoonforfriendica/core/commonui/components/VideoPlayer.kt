package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import android.media.session.PlaybackState
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlin.math.abs

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
    onPlaybackStarted: (() -> Unit)?,
    onResize: ((IntSize) -> Unit)?,
) {
    val context = LocalContext.current
    var started = false
    val exoPlayer =
        remember {
            ExoPlayer
                .Builder(context)
                .build()
                .apply {
                    val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                    val dataSourceFactory: DataSource.Factory =
                        DefaultDataSource.Factory(
                            context,
                            defaultDataSourceFactory,
                        )
                    val source =
                        ProgressiveMediaSource
                            .Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(url))
                    setMediaSource(source)

                    addListener(
                        object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                super.onPlaybackStateChanged(playbackState)
                                if (playbackState == PlaybackState.STATE_PLAYING) {
                                    if (!started) {
                                        onPlaybackStarted?.invoke()
                                        started = true
                                    }
                                }
                            }
                        },
                    )
                    prepare()
                }.apply {
                    videoScalingMode = C.VIDEO_SCALING_MODE_DEFAULT
                    repeatMode = Player.REPEAT_MODE_ALL
                }
        }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    controllerAutoShow = true
                    controllerHideOnTouch = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)

                    videoSurfaceView?.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
                        val newHeight = abs(bottom - top)
                        val newWidth = abs(right - left)
                        onResize?.invoke(IntSize(width = newWidth, height = newHeight))
                    }

                    player = exoPlayer
                }
            },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}
