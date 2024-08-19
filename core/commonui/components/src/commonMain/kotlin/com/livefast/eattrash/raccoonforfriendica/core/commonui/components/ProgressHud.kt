package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ProgressHud(
    overlayColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        contentColor = Color.Transparent,
        color = overlayColor,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = color,
            )
        }
    }
}
