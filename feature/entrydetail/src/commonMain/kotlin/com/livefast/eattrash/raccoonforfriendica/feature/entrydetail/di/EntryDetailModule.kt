package com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.di

import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.entrydetail.EntryDetailViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

data class EntryDetailMviModelParams(
    val id: String,
    val swipeNavigationEnabled: Boolean,
)

val entryDetailModule =
    DI.Module("EntryDetailModule") {
        bind<EntryDetailMviModel> {
            factory { params: EntryDetailMviModelParams ->
                EntryDetailViewModel(
                    id = params.id,
                    swipeNavigationEnabled = params.swipeNavigationEnabled,
                    timelineEntryRepository = instance(),
                    identityRepository = instance(),
                    settingsRepository = instance(),
                    userRepository = instance(),
                    hapticFeedback = instance(),
                    entryCache = instance(),
                    imagePreloadManager = instance(),
                    blurHashRepository = instance(),
                    emojiHelper = instance(),
                    replyHelper = instance(),
                    imageAutoloadObserver = instance(),
                    toggleEntryFavorite = instance(),
                    toggleEntryDislike = instance(),
                    getTranslation = instance(),
                    timelineNavigationManager = instance(),
                    notificationCenter = instance(),
                )
            }
        }
    }
