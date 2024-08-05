package com.livefast.eattrash.raccoonforfriendica.bottomnavigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object ExploreTab : Tab {
    override val options: TabOptions
        @Composable
        get() =
            TabOptions(
                index = 1u,
                title = "",
            )

    @Composable
    override fun Content() {
        Text("Explore tab")
    }
}
