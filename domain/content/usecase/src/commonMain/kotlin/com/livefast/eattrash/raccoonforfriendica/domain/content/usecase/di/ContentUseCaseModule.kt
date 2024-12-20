package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultExportUserListUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserListUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val contentUseCaseModule =
    DI.Module("ContentUseCaseModule") {
        bind<ExportUserListUseCase> {
            singleton {
                DefaultExportUserListUseCase(
                    userRepository = instance(),
                )
            }
        }
    }
