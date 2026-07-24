package com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.di

import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.AcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.datasource.DefaultAcknowledgementsRemoteDataSource
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.main.AcknowledgementsViewModel
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository.AcknowledgementsRepository
import com.livefast.eattrash.raccoonforfriendica.feat.acknowledgements.repository.DefaultAcknowledgementsRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val acknowledgementsModule = module {
    single<AcknowledgementsRemoteDataSource> {
        DefaultAcknowledgementsRemoteDataSource()
    }
    single<AcknowledgementsRepository> {
        DefaultAcknowledgementsRepository(dataSource = get())
    }
    viewModel {
        AcknowledgementsViewModel(
            acknowledgementsRepository = get(),
        )
    }
}
