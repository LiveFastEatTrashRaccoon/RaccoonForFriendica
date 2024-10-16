package com.livefast.eattrash.raccoonforfriendica.feature.composer.di

import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.composer.utils.DefaultPrepareForPreviewUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.utils.PrepareForPreviewUseCase
import org.koin.dsl.module

val featureComposerModule =
    module {
        single<PrepareForPreviewUseCase> {
            DefaultPrepareForPreviewUseCase(
                apiConfigurationRepository = get(),
            )
        }
        factory<ComposerMviModel> { params ->
            ComposerViewModel(
                inReplyToId = params[0],
                timelineEntryRepository = get(),
                photoRepository = get(),
                identityRepository = get(),
                userPaginationManager = get(),
                circlesRepository = get(),
                nodeInfoRepository = get(),
                supportedFeatureRepository = get(),
                mediaRepository = get(),
                albumRepository = get(),
                albumPhotoPaginationManager = get(),
                entryCache = get(),
                scheduledEntryRepository = get(),
                draftRepository = get(),
                settingsRepository = get(),
                emojiRepository = get(),
                userRepository = get(),
                apiConfigurationRepository = get(),
                notificationCenter = get(),
            )
        }
    }
