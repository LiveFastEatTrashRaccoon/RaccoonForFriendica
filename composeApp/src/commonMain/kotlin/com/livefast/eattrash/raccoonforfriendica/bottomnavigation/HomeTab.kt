package com.livefast.eattrash.raccoonforfriendica.bottomnavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.livefast.eattrash.raccoonforfriendica.core.navigation.ScreenContent
import com.livefast.eattrash.raccoonforfriendica.feature.timeline.TimelineScreen

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 0u,
                title = "",
            )

    @Composable
    override fun Content() {
        ScreenContent(TimelineScreen())
    }
}
