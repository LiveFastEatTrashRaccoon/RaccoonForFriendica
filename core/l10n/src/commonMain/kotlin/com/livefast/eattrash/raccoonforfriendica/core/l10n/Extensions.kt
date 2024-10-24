package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales

@Composable
fun String?.toLanguageName(): String? =
    when (this) {
        Locales.EN -> LocalStrings.current.languageEn
        Locales.DE -> LocalStrings.current.languageDe
        Locales.FR -> LocalStrings.current.languageFr
        Locales.ES -> LocalStrings.current.languageEs
        Locales.IT -> LocalStrings.current.languageIt
        Locales.PL -> LocalStrings.current.languagePl
        Locales.PT -> LocalStrings.current.languagePt
        else -> null
    }

fun String?.toLanguageFlag(): String? =
    when (this) {
        Locales.EN -> "🇬🇧"
        Locales.DE -> "🇩🇪"
        Locales.ES -> "🇪🇸"
        Locales.FR -> "🇫🇷"
        Locales.IT -> "🇮🇹"
        Locales.PL -> "🇵🇱"
        Locales.PT -> "🇵🇹"
        else -> null
    }
