package com.github.akesiseli.raccoonforfriendica.core.l10n.messages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.lyricist.LanguageTag
import cafe.adriel.lyricist.Lyricist
import cafe.adriel.lyricist.ProvideStrings

interface Strings {
    val systemDefault: String
    val settingsThemeLight: String
    val settingsThemeDark: String
    val settingsThemeBlack: String
    val sectionTitleHome: String
    val sectionTitleExplore: String
    val sectionTitleInbox: String
    val sectionTitleProfile: String
    val barThemeTransparent: String
    val barThemeOpaque: String
    val timelineAll: String
    val timelineSubscriptions: String
    val nodeVia: String
}

object Locales {
    const val EN = "en"
    const val IT = "it"
}

internal val localizableStrings: Map<LanguageTag, Strings> =
    mapOf(
        Locales.EN to EnStrings,
        Locales.IT to ItStrings,
    )

val LocalStrings: ProvidableCompositionLocal<Strings> =
    staticCompositionLocalOf { EnStrings }

@Composable
fun ProvideStrings(
    lyricist: Lyricist<Strings>,
    content: @Composable () -> Unit,
) {
    ProvideStrings(lyricist, LocalStrings, content)
}
