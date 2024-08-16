package com.livefast.eattrash.raccoonforfriendica.feature.thread.di

import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.thread.ThreadViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase.DefaultPopulateThreadUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase.PopulateThreadUseCase
import org.koin.dsl.module

val featureThreadModule =
    module {
        single<PopulateThreadUseCase> {
            DefaultPopulateThreadUseCase(
                timelineEntryRepository = get(),
            )
        }
        factory<ThreadMviModel> { params ->
            ThreadViewModel(
                entryId = params[0],
                populateThreadUseCase = get(),
                timelineEntryRepository = get(),
            )
        }
    }
