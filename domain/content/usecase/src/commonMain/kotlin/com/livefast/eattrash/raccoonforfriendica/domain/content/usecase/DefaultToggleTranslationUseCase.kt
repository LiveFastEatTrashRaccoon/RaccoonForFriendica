package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultToggleTranslationUseCase(private val getTranslation: GetTranslationUseCase) : ToggleTranslationUseCase {
    override suspend operator fun invoke(entry: TimelineEntryModel, targetLang: String): TimelineEntryModel {
        val translation: TimelineEntryModel?
        val provider: String?
        when {
            !entry.isShowingTranslation && entry.translation == null -> {
                val result = getTranslation(entry = entry, targetLang = targetLang)
                translation = result?.target
                provider = result?.provider
            }

            else -> {
                translation = entry.translation
                provider = entry.translationProvider
            }
        }
        return entry.copy(
            isShowingTranslation = translation != null && !entry.isShowingTranslation,
            translation = translation,
            translationProvider = provider.takeIf { translation != null },
            translationLoading = false,
        )
    }
}
