package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.TimelineLayout
import com.livefast.eattrash.raccoonforfriendica.core.appearance.theme.Spacing

@Composable
fun TimelineDivider(modifier: Modifier = Modifier, layout: TimelineLayout = TimelineLayout.Full) {
    when (layout) {
        TimelineLayout.Card ->
            Spacer(
                modifier = modifier.height(Spacing.s),
            )

        else ->
            HorizontalDivider(
                modifier = modifier.padding(vertical = Spacing.s),
            )
    }
}
