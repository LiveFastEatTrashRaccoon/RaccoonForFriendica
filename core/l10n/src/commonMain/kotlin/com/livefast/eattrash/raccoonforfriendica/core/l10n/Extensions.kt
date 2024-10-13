package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales

@Composable
fun String?.toLanguageName(): String? =
    when (this) {
        Locales.EN -> LocalStrings.current.languageEn
        Locales.IT -> LocalStrings.current.languageIt
        Locales.DE -> LocalStrings.current.languageDe
        Locales.FR -> LocalStrings.current.languageFr
        Locales.ES -> LocalStrings.current.languageEs
        else -> null
    }

fun String?.toLanguageFlag(): String? =
    when (this) {
        Locales.EN -> "🇬🇧"
        Locales.IT -> "🇮🇹"
        Locales.DE -> "🇩🇪"
        Locales.FR -> "🇫🇷"
        Locales.ES -> "🇪🇸"
        else -> null
    }
