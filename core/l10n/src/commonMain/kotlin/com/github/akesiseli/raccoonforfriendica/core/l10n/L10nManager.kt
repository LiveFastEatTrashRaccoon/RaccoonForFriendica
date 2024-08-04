package com.github.akesiseli.raccoonforfriendica.core.l10n

import cafe.adriel.lyricist.Lyricist
import com.github.akesiseli.raccoonforfriendica.core.l10n.messages.Strings

interface L10nManager {
    val lyricist: Lyricist<Strings>

    fun changeLanguage(lang: String)
}
