package com.livefast.eattrash.raccoonforfriendica.core.l10n

import android.os.Build
import java.util.Locale

internal actual fun replaceLang(lang: String) {
    val tokens = lang.split("_")
    val country = tokens.getOrNull(1).orEmpty()
    val langCode = tokens.firstOrNull() ?: Locales.EN
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        Locale.of(langCode, country)
    } else {
        Locale(langCode, country)
    }
    Locale.setDefault(locale)
}
