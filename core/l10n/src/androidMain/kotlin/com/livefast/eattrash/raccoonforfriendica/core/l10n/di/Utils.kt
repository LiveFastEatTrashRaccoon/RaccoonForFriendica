package com.livefast.eattrash.raccoonforfriendica.core.l10n.di

import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Strings
import org.koin.java.KoinJavaComponent
import java.util.Locale

actual fun getL10nManager(): L10nManager {
    val res: L10nManager by KoinJavaComponent.inject(L10nManager::class.java)
    return res
}

internal actual fun replaceLang(lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
}

actual fun getStrings(lang: String): Strings {
    val res: Strings by KoinJavaComponent.inject(Strings::class.java)
    return res
}
