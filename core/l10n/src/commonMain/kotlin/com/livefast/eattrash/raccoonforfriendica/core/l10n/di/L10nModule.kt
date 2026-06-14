package com.livefast.eattrash.raccoonforfriendica.core.l10n.di

import com.livefast.eattrash.raccoonforfriendica.core.l10n.DefaultL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.DefaultStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindFactory
import org.kodein.di.bindSingleton
import org.kodein.di.singleton

val l10nModule =
    DI.Module("L10nModule") {
        bindSingleton<L10nManager> { DefaultL10nManager() }
        bindFactory<String, Strings> { _ ->
            // the lang parameter is currently ignored
            DefaultStrings()
        }
    }
