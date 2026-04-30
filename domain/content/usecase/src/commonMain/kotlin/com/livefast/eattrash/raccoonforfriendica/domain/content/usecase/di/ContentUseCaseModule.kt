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
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val contentUseCaseModule =
    DI.Module("ContentUseCaseModule") {
        bindSingleton<ExportUserListUseCase> {
            DefaultExportUserListUseCase(
                userRepository = instance(),
            )
        }
        bindSingleton<ToggleEntryFavoriteUseCase> {
            DefaultToggleEntryFavoriteUseCase(
                entryRepository = instance(),
            )
        }
        bindSingleton<ToggleEntryDislikeUseCase> {
            DefaultToggleEntryDislikeUseCase(
                entryRepository = instance(),
            )
        }
        bindSingleton<BBCodeConverter> {
            DefaultBBCodeConverter()
        }
        bindSingleton<MarkdownConverter> {
            DefaultMarkdownConverter()
        }
        bindSingleton<StripMarkupUseCase> {
            DefaultStripMarkupUseCase(
                bbCodeConverter = instance(),
                markdownConverter = instance(),
            )
        }
        bindSingleton<GetTranslationUseCase> {
            DefaultGetTranslationUseCase(
                supportedFeatureRepository = instance(),
                defaultRepository = instance(),
                fallbackRepository = instance(),
                stripMarkup = instance(),
                translationProviderConfigStore = instance(),
            )
        }
        bindSingleton<PopulateThreadUseCase> {
            DefaultPopulateThreadUseCase(
                timelineEntryRepository = instance(),
                emojiHelper = instance(),
            )
        }
        bindSingleton<GetInnerUrlUseCase> {
            DefaultGetInnerUrlUseCase(
                apiConfigurationRepository = instance(),
            )
        }
    }
