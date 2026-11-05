package com.livefast.eattrash.raccoonforfriendica.core.commonui.components

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomShapeLoader(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    LoadingIndicator(
        modifier = modifier,
        color = color,
    )
}

@Composable
@Preview
private fun CustomShapeLoaderPreview() {
    CustomShapeLoader()
}
