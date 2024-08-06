package com.livefast.eattrash.raccoonforfriendica.core.l10n

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.Locales

@Composable
fun String?.toLanguageName(): String? =
    when (this) {
        Locales.IT -> LocalStrings.current.languageIt
        Locales.EN -> LocalStrings.current.languageEn
        else -> null
    }
