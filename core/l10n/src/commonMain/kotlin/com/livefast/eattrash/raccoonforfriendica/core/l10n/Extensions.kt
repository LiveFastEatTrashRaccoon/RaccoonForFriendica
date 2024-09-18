package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales

@Composable
fun String?.toLanguageName(): String? =
    when (this) {
        Locales.EN -> LocalStrings.current.languageEn
        Locales.DE -> LocalStrings.current.languageDe
        Locales.IT -> LocalStrings.current.languageIt
        else -> null
    }

fun String?.toLanguageFlag(): String? =
    when (this) {
        Locales.IT -> "🇮🇹"
        Locales.EN -> "🇬🇧"
        Locales.DE -> "🇩🇪"
        else -> null
    }
