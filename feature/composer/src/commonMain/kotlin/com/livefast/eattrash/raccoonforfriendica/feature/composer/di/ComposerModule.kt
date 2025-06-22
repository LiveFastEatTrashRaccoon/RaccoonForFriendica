package com.livefast.eattrash.raccoonforfriendica.feature.composer.di

import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.ViewModelCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.architecture.di.bindViewModelWithArgs
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.DefaultPrepareForPreviewUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.PrepareForPreviewUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

data class ComposerViewModelArgs(val inReplyToId: String) : ViewModelCreationArgs

val composerModule =
    DI.Module("ComposerModule") {
        bind<PrepareForPreviewUseCase> {
            singleton {
                DefaultPrepareForPreviewUseCase(
                    apiConfigurationRepository = instance(),
                    bbCodeConverter = instance(),
                    markdownConverter = instance(),
                )
            }
        }
        bindViewModelWithArgs { args: ComposerViewModelArgs ->
            ComposerViewModel(
                inReplyToId = args.inReplyToId,
                identityRepository = instance(),
                timelineEntryRepository = instance(),
                photoRepository = instance(),
                userPaginationManager = instance(),
                circlesRepository = instance(),
                nodeInfoRepository = instance(),
                supportedFeatureRepository = instance(),
                mediaRepository = instance(),
                albumRepository = instance(),
                albumPhotoPaginationManager = instance(),
                entryCache = instance(),
                scheduledEntryRepository = instance(),
                draftRepository = instance(),
                settingsRepository = instance(),
                emojiRepository = instance(),
                userRepository = instance(),
                searchRepository = instance(),
                prepareForPreview = instance(),
                stripMarkup = instance(),
                bbCodeConverter = instance(),
                notificationCenter = instance(),
            )
        }
    }
