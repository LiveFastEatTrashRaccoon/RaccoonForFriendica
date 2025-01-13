package com.livefast.eattrash.raccoonforfriendica.feature.composer.di

import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerMviModel
import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.DefaultBBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.DefaultMarkdownConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.converters.MarkdownConverter
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.DefaultPrepareForPreviewUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.DefaultStripMarkupUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.PrepareForPreviewUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.StripMarkupUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.singleton

val composerModule =
    DI.Module("ComposerModule") {
        bind<BBCodeConverter> {
            singleton { DefaultBBCodeConverter() }
        }
        bind<MarkdownConverter> {
            singleton { DefaultMarkdownConverter() }
        }
        bind<PrepareForPreviewUseCase> {
            singleton {
                DefaultPrepareForPreviewUseCase(
                    apiConfigurationRepository = instance(),
                    bbCodeConverter = instance(),
                    markdownConverter = instance(),
                )
            }
        }
        bind<StripMarkupUseCase> {
            singleton {
                DefaultStripMarkupUseCase(
                    prepareForPreview = instance(),
                )
            }
        }
        bind<ComposerMviModel> {
            factory { inReplyToId: String ->
                ComposerViewModel(
                    inReplyToId = inReplyToId,
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
    }
