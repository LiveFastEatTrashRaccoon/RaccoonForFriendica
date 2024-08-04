package com.github.akesiseli.raccoonforfriendica.core.appearance.repository

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.github.akesiseli.raccoonforfriendica.core.appearance.data.UiFontFamily
import com.github.akesiseli.raccoonforfriendica.core.appearance.data.UiTheme
import kotlinx.coroutines.flow.StateFlow

@Stable
interface ThemeRepository {
    val uiTheme: StateFlow<UiTheme?>
    val fontFamily: StateFlow<UiFontFamily>
    val dynamicColors: StateFlow<Boolean>
    val customSeedColor: StateFlow<Color?>

    fun changeUiTheme(value: UiTheme?)

    fun changeFontFamily(value: UiFontFamily)

    fun changeDynamicColors(value: Boolean)

    fun changeCustomSeedColor(value: Color?)
}
