package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforfriendica.core.appearance.data.UiTheme

data class SettingsModel(
    val id: Long = 0,
    val accountId: Long = 0,
    val lang: String = "en",
    val theme: UiTheme = UiTheme.Default,
    val fontFamily: UiFontFamily = UiFontFamily.Default,
    val dynamicColors: Boolean = false,
    val customSeedColor: Int? = null,
    val defaultTimelineType: Int = 0,
    val includeNsfw: Boolean = false,
    val blurNsfw: Boolean = true,
)
