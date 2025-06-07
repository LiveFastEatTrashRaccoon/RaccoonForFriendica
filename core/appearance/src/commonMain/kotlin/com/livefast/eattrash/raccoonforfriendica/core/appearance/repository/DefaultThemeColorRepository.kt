package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor

internal class DefaultThemeColorRepository : ThemeColorRepository {
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
