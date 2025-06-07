package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.CornerSize
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforfriendica.core.utils.compose.shimmerEffect

@Composable
fun GenericPlaceholder(modifier: Modifier = Modifier, height: Dp = 100.dp) {
    Column(
        modifier = modifier.padding(horizontal = Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        Box(
            modifier =
            Modifier
                .height(height)
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerSize.s))
                .shimmerEffect(),
        )
    }
}
