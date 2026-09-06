package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun String?.toLanguageName(): String? = when (this) {
    Locales.EN -> LocalStrings.current.languageEn
    Locales.DE -> LocalStrings.current.languageDe
    Locales.FI -> LocalStrings.current.languageFi
    Locales.FR -> LocalStrings.current.languageFr
    Locales.ES -> LocalStrings.current.languageEs
    Locales.IT -> LocalStrings.current.languageIt
    Locales.PL -> LocalStrings.current.languagePl
    Locales.PT -> LocalStrings.current.languagePt
    Locales.PT_BR -> LocalStrings.current.languagePtBr
    Locales.RO -> LocalStrings.current.languageRo
    Locales.RU -> LocalStrings.current.languageRu
    Locales.TA -> LocalStrings.current.languageTa
    Locales.UK -> LocalStrings.current.languageUk
    else -> null
}

fun String.toLanguageDirection(): LayoutDirection = when (this) {
    "ar" -> LayoutDirection.Rtl
    else -> LayoutDirection.Ltr
}

fun String?.toLanguageFlag(): String? = when (this) {
    Locales.EN -> "🇬🇧"
    Locales.DE -> "🇩🇪"
    Locales.ES -> "🇪🇸"
    Locales.FI -> "🇫🇮"
    Locales.FR -> "🇫🇷"
    Locales.IT -> "🇮🇹"
    Locales.PL -> "🇵🇱"
    Locales.PT -> "🇵🇹"
    Locales.PT_BR -> "🇧🇷"
    Locales.RO -> "🇷🇴"
    Locales.RU -> "🇷🇺"
    Locales.TA -> "🇮🇳"
    Locales.UK -> "🇺🇦"
    else -> null
}
