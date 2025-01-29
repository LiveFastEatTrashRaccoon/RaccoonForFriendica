package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.di

import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list.ShortcutListViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val shortcutsModule =
    DI.Module("ShortcutsModule") {
        bind<ShortcutListMviModel> {
            provider {
                ShortcutListViewModel(
                    shortcutRepository = instance(),
                    accountRepository = instance(),
                    settingsRepository = instance(),
                )
            }
        }
    }
