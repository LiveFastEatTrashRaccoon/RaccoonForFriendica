package com.github.akesiseli.raccoonforfriendica.di

import com.github.akesiseli.raccoonforfriendica.main.MainMviModel
import com.github.akesiseli.raccoonforfriendica.main.MainViewModel
import org.koin.dsl.module

internal val sharedModule =
    module {
        factory<MainMviModel> {
            MainViewModel()
        }
    }
