package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales

@Composable
fun String?.toLanguageName(): String? =
    when (this) {
        Locales.EN -> LocalStrings.current.languageEn
        Locales.DE -> LocalStrings.current.languageDe
        Locales.FI -> LocalStrings.current.languageFi
        Locales.FR -> LocalStrings.current.languageFr
        Locales.ES -> LocalStrings.current.languageEs
        Locales.IT -> LocalStrings.current.languageIt
        Locales.PL -> LocalStrings.current.languagePl
        Locales.PT -> LocalStrings.current.languagePt
        Locales.UA -> LocalStrings.current.languageUa
        else -> null
    }

fun String?.toLanguageFlag(): String? =
    when (this) {
        Locales.EN -> "🇬🇧"
        Locales.DE -> "🇩🇪"
        Locales.ES -> "🇪🇸"
        Locales.FI -> "🇫🇮"
        Locales.FR -> "🇫🇷"
        Locales.IT -> "🇮🇹"
        Locales.PL -> "🇵🇱"
        Locales.PT -> "🇵🇹"
        Locales.UA -> "🇺🇦"
        else -> null
    }
