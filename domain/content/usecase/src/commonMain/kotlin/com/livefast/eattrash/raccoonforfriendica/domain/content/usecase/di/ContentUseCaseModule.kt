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
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val contentUseCaseModule =
    DI.Module("ContentUseCaseModule") {
        bind<ExportUserListUseCase> {
            singleton {
                DefaultExportUserListUseCase(
                    userRepository = instance(),
                )
            }
        }
        bind<ToggleEntryFavoriteUseCase> {
            singleton {
                DefaultToggleEntryFavoriteUseCase(
                    entryRepository = instance(),
                )
            }
        }
        bind<ToggleEntryDislikeUseCase> {
            singleton {
                DefaultToggleEntryDislikeUseCase(
                    entryRepository = instance(),
                )
            }
        }
        bind<BBCodeConverter> {
            singleton { DefaultBBCodeConverter() }
        }
        bind<MarkdownConverter> {
            singleton { DefaultMarkdownConverter() }
        }
        bind<StripMarkupUseCase> {
            singleton {
                DefaultStripMarkupUseCase(
                    bbCodeConverter = instance(),
                    markdownConverter = instance(),
                )
            }
        }
        bind<GetTranslationUseCase> {
            singleton {
                DefaultGetTranslationUseCase(
                    supportedFeatureRepository = instance(),
                    defaultRepository = instance(tag = "default"),
                    fallbackRepository = instance(tag = "fallback"),
                    stripMarkup = instance(),
                )
            }
        }
        bind<PopulateThreadUseCase> {
            singleton {
                DefaultPopulateThreadUseCase(
                    timelineEntryRepository = instance(),
                    emojiHelper = instance(),
                )
            }
        }
        bind<GetInnerUrlUseCase> {
            singleton {
                DefaultGetInnerUrlUseCase(
                    apiConfigurationRepository = instance(),
                )
            }
        }
    }
