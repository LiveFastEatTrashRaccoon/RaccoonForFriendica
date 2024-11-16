package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultExportUserListUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserListUseCase
import org.koin.dsl.module

val domainContentUseCaseModule =
    module {
        single<ExportUserListUseCase> {
            DefaultExportUserListUseCase(
                userRepository = get(),
            )
        }
    }
