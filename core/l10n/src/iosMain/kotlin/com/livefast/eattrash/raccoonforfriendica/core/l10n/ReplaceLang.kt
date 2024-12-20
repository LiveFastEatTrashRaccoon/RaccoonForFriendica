package com.livefast.eattrash.raccoonforfriendica.core.l10n

import platform.Foundation.NSUserDefaults

internal actual fun replaceLang(lang: String) {
    NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang), "AppleLanguages")
}
