package com.livefast.eattrash.raccoonforfriendica.core.l10n.di

import com.livefast.eattrash.raccoonforfriendica.core.l10n.DefaultL10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.DefaultStrings
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import org.koin.dsl.module

val l10nModule = module {
    single<L10nManager> { DefaultL10nManager() }
    factory<Strings> { params ->
        // the lang parameter is currently ignored
        val lang: String = params.get()
        DefaultStrings()
    }
}
