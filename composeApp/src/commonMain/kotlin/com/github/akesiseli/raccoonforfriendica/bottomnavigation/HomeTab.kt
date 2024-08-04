package com.github.akesiseli.raccoonforfriendica.bottomnavigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.akesiseli.raccoonforfriendica.feature.timeline.TimelineScreen

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
        Navigator(TimelineScreen())
    }
}
