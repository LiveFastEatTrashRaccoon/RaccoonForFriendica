package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.di

import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultExportUserListUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultGetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultGetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultPopulateThreadUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultStripMarkupUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.DefaultToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ExportUserListUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetInnerUrlUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.GetTranslationUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.PopulateThreadUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.StripMarkupUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryDislikeUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.ToggleEntryFavoriteUseCase
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.DefaultBBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.DefaultMarkdownConverter
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.MarkdownConverter
import org.koin.dsl.module

val contentUseCaseModule = module {
    single<ExportUserListUseCase> {
        DefaultExportUserListUseCase(
            userRepository = get(),
        )
    }
    single<ToggleEntryFavoriteUseCase> {
        DefaultToggleEntryFavoriteUseCase(
            entryRepository = get(),
        )
    }
    single<ToggleEntryDislikeUseCase> {
        DefaultToggleEntryDislikeUseCase(
            entryRepository = get(),
        )
    }
    single<BBCodeConverter> {
        DefaultBBCodeConverter()
    }
    single<MarkdownConverter> {
        DefaultMarkdownConverter()
    }
    single<StripMarkupUseCase> {
        DefaultStripMarkupUseCase(
            bbCodeConverter = get(),
            markdownConverter = get(),
        )
    }
    single<GetTranslationUseCase> {
        DefaultGetTranslationUseCase(
            supportedFeatureRepository = get(),
            defaultRepository = get(),
            fallbackRepository = get(),
            stripMarkup = get(),
            translationProviderConfigStore = get(),
        )
    }
    single<PopulateThreadUseCase> {
        DefaultPopulateThreadUseCase(
            timelineEntryRepository = get(),
            emojiHelper = get(),
        )
    }
    single<GetInnerUrlUseCase> {
        DefaultGetInnerUrlUseCase(
            apiConfigurationRepository = get(),
        )
    }
}
