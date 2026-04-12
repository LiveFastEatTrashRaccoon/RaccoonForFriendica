package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TranslatedTimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.FallbackTranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TranslationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsArgAt
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultGetTranslationUseCaseTest {
    private val supportedFeatureRepository = mock<SupportedFeatureRepository>()
    private val defaultRepository = mock<TranslationRepository>()
    private val fallbackRepository = mock<FallbackTranslationRepository>()
    private val stripMarkupUseCase = mock<StripMarkupUseCase> {
        every { invoke(any(), any()) } returnsArgAt 0
    }

    private val translationProviderConfigStore = mock<TranslationProviderConfigStore>()

    private val sut =
        DefaultGetTranslationUseCase(
            supportedFeatureRepository = supportedFeatureRepository,
            defaultRepository = defaultRepository,
            fallbackRepository = fallbackRepository,
            stripMarkup = stripMarkupUseCase,
            translationProviderConfigStore = translationProviderConfigStore,
        )

    @Test
    fun `given translation Mastodon API supported when invoked then result and interactions are as expected`() =
        runTest {
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = "source text",
                )
            val targetEntry =
                TimelineEntryModel(
                    id = "1",
                    content = "target text",
                )
            val targetLang = "it"
            val expected =
                TranslatedTimelineEntryModel(
                    source = entry,
                    provider = "TAS",
                    target = targetEntry,
                )
            every {
                supportedFeatureRepository.features
            } returns MutableStateFlow(NodeFeatures(supportsTranslation = true))
            everySuspend {
                defaultRepository.getTranslation(any(), any())
            } returns expected

            val res =
                sut(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertEquals(res, expected)
            verifySuspend {
                defaultRepository.getTranslation(entry, targetLang)
            }
            verifySuspend(VerifyMode.not) {
                stripMarkupUseCase(any(), any())
                translationProviderConfigStore.getDefaultId()
                translationProviderConfigStore.setDefaultId(any())
                fallbackRepository.getTranslation(any(), any(), any())
            }
        }

    @Test
    fun `given translation Mastodon API unavailable when invoked then result and interactions are as expected`() =
        runTest {
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = "source text",
                )
            val targetEntry =
                TimelineEntryModel(
                    id = "1",
                    content = "target text",
                )
            val targetLang = "it"
            val expected =
                TranslatedTimelineEntryModel(
                    source = entry,
                    provider = "TAS",
                    target = targetEntry,
                )
            every {
                supportedFeatureRepository.features
            } returns MutableStateFlow(NodeFeatures(supportsTranslation = true))
            everySuspend { defaultRepository.getTranslation(any(), any()) } returns null
            val defaultConfigId = "default-config-id"
            val defaultConfig = TranslationProviderConfig(name = "DUMMY", url = "")
            everySuspend { translationProviderConfigStore.getDefaultId() } returns defaultConfigId
            everySuspend { translationProviderConfigStore.getById(defaultConfigId) } returns defaultConfig
            everySuspend {
                fallbackRepository.getTranslation(any(), any(), any())
            } returns expected

            val res =
                sut(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertEquals(res, expected)
            verifySuspend {
                stripMarkupUseCase("source text", MarkupMode.HTML)
                defaultRepository.getTranslation(any(), any())
                translationProviderConfigStore.getDefaultId()
                translationProviderConfigStore.getById(defaultConfigId)
                fallbackRepository.getTranslation(entry, targetLang, defaultConfig)
            }
        }

    @Test
    fun `given translation Mastodon API not supported when invoked then result and interactions are as expected`() =
        runTest {
            val entry =
                TimelineEntryModel(
                    id = "1",
                    content = "source text",
                )
            val targetEntry =
                TimelineEntryModel(
                    id = "1",
                    content = "target text",
                )
            val targetLang = "it"
            val expected =
                TranslatedTimelineEntryModel(
                    source = entry,
                    provider = "TAS",
                    target = targetEntry,
                )
            every { supportedFeatureRepository.features } returns MutableStateFlow(NodeFeatures())
            val defaultConfigId = "default-config-id"
            val defaultConfig = TranslationProviderConfig(name = "DUMMY", url = "")
            everySuspend { translationProviderConfigStore.getDefaultId() } returns defaultConfigId
            everySuspend { translationProviderConfigStore.getById(defaultConfigId) } returns defaultConfig
            everySuspend {
                fallbackRepository.getTranslation(any(), any(), any())
            } returns expected

            val res =
                sut(
                    entry = entry,
                    targetLang = targetLang,
                )

            assertEquals(res, expected)
            verifySuspend(VerifyMode.not) {
                defaultRepository.getTranslation(any(), any())
            }
            verifySuspend {
                stripMarkupUseCase("source text", MarkupMode.HTML)
                translationProviderConfigStore.getDefaultId()
                translationProviderConfigStore.getById(defaultConfigId)
                fallbackRepository.getTranslation(entry, targetLang, defaultConfig)
            }
        }
}
