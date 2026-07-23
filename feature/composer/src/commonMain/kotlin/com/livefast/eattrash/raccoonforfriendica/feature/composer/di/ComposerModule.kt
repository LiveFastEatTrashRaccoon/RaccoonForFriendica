package com.livefast.eattrash.raccoonforfriendica.feature.composer.di

import com.livefast.eattrash.raccoonforfriendica.feature.composer.ComposerViewModel
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.DefaultPrepareForPreviewUseCase
import com.livefast.eattrash.raccoonforfriendica.feature.composer.usecase.PrepareForPreviewUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class ComposerViewModelArgs(val inReplyToId: String?, val quotedId: String?)

val composerModule = module {
    single<PrepareForPreviewUseCase> {
        DefaultPrepareForPreviewUseCase(
            apiConfigurationRepository = get(),
            bbCodeConverter = get(),
            markdownConverter = get(),
        )
    }
    viewModel { params ->
        val args: ComposerViewModelArgs = params.get()
        ComposerViewModel(
            inReplyToId = args.inReplyToId,
            quotedId = args.quotedId,
            identityRepository = get(),
            timelineEntryRepository = get(),
            photoRepository = get(),
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
            searchRepository = get(),
            prepareForPreview = get(),
            stripMarkup = get(),
            bbCodeConverter = get(),
            notificationCenter = get(),
        )
    }
}
