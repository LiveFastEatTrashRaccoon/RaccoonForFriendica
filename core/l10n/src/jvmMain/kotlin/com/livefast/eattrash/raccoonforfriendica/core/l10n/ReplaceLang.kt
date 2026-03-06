package com.livefast.eattrash.raccoonforfriendica.core.l10n

import java.util.Locale

internal actual fun replaceLang(lang: String) {
    val tokens = lang.split("_")
    val country = tokens.getOrNull(1).orEmpty()
    val langCode = tokens.firstOrNull() ?: Locales.EN
    Locale.setDefault(Locale.of(langCode, country))
}
