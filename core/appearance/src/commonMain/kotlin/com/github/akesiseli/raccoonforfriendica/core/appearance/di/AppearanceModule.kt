package com.github.akesiseli.raccoonforfriendica.core.appearance.di

import com.github.akesiseli.raccoonforfriendica.core.appearance.repository.DefaultThemeRepository
import com.github.akesiseli.raccoonforfriendica.core.appearance.repository.ThemeRepository
import org.koin.dsl.module

val coreAppearanceModule =
    module {
        includes(nativeAppearanceModule)

        single<ThemeRepository> {
            DefaultThemeRepository()
        }
    }
