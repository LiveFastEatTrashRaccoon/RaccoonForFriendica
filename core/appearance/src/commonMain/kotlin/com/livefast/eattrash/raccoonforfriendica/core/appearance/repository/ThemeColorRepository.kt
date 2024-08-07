package com.livefast.eattrash.raccoonforfriendica.core.appearance.repository

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.ThemeColor

interface ThemeColorRepository {
    fun getColors(): List<ThemeColor>
}
