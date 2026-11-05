package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    CircularWavyProgressIndicator(
        modifier = modifier,
        color = color,
        trackColor = trackColor,
    )
}

@Composable
fun CustomProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    CircularWavyProgressIndicator(
        modifier = modifier,
        progress = progress,
        color = color,
        trackColor = trackColor,
    )
}

@Composable
@Preview
private fun CustomProgressIndicatorPreview() {
    CircularWavyProgressIndicator()
}
