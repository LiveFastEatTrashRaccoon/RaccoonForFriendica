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
import org.koin.dsl.module

val featureComposerModule =
    module {
        single<BBCodeConverter> {
            DefaultBBCodeConverter()
        }
        single<MarkdownConverter> {
            DefaultMarkdownConverter()
        }
        single<PrepareForPreviewUseCase> {
            DefaultPrepareForPreviewUseCase(
                apiConfigurationRepository = get(),
                bbCodeConverter = get(),
                markdownConverter = get(),
            )
        }
        single<StripMarkupUseCase> {
            DefaultStripMarkupUseCase(
                prepareForPreview = get(),
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
                prepareForPreview = get(),
                stripMarkup = get(),
                notificationCenter = get(),
                bbCodeConverter = get(),
            )
        }
    }
