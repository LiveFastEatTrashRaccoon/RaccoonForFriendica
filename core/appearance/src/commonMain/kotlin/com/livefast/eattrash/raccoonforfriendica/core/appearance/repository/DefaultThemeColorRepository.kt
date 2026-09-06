package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultThemeColorRepository : ThemeColorRepository {
    override fun getColors(): List<ThemeColor> = listOf(
        ThemeColor.Purple,
        ThemeColor.Blue,
        ThemeColor.LightBlue,
        ThemeColor.Green,
        ThemeColor.Yellow,
        ThemeColor.Orange,
        ThemeColor.Red,
        ThemeColor.Pink,
        ThemeColor.Gray,
        ThemeColor.White,
    )
}
