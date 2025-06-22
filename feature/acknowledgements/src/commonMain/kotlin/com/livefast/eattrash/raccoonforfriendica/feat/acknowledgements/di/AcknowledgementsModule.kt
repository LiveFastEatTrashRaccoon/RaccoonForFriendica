package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModel
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.DefaultAcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main.AcknowledgementsViewModel
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository.AcknowledgementsRepository
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository.DefaultAcknowledgementsRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val acknowledgementsModule =
    DI.Module("AcknowledgementsModule") {
        bind<AcknowledgementsRemoteDataSource> {
            singleton {
                DefaultAcknowledgementsRemoteDataSource()
            }
        }
        bind<AcknowledgementsRepository> {
            singleton {
                DefaultAcknowledgementsRepository(dataSource = instance())
            }
        }
        bindViewModel {
            AcknowledgementsViewModel(
                acknowledgementsRepository = instance(),
            )
        }
    }
