package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.EntryProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.UserProcessor
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test

class DefaultCustomUriHandlerTest {
    private val defaultHandler = mock<UriHandler>(mode = MockMode.autoUnit)
    private val detailOpener = mock<DetailOpener>(mode = MockMode.autoUnit)
    private val customTabsHelper =
        mock<CustomTabsHelper>(mode = MockMode.autoUnit) {
            every { isSupported } returns false
        }
    private val settingsRepository =
        mock<SettingsRepository> {
            every { current } returns MutableStateFlow(SettingsModel())
        }
    private val hashtagProcessor =
        mock<HashtagProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val userProcessor =
        mock<UserProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val entryProcessor =
        mock<EntryProcessor> {
            everySuspend { process(any()) } returns false
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sut =
        DefaultCustomUriHandler(
            defaultHandler = defaultHandler,
            customTabsHelper = customTabsHelper,
            settingsRepository = settingsRepository,
            detailOpener = detailOpener,
            hashtagProcessor = hashtagProcessor,
            userProcessor = userProcessor,
            entryProcessor = entryProcessor,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `given URL and external mode when openUri then interactions are as expected`() {
        val url = "https://www.example.com"

        sut.openUri(url)

        verify {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given URL and custom tabs mode and custom tabs not supported when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val url = "https://www.example.com"

        sut.openUri(url)

        verify {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given URL and custom tabs mode and custom tabs supported when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        every { customTabsHelper.isSupported } returns true
        val url = "https://www.example.com"

        sut.openUri(url)

        verify {
            customTabsHelper.handle(url)
        }
    }

    @Test
    fun `given URL and internal mode when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.Internal))
        val url = "https://www.example.com"

        sut.openUri(url)

        verify {
            detailOpener.openInternalWebView(url)
        }
    }

    @Test
    fun `given hashtag URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel())
        everySuspend { hashtagProcessor.process(any()) } returns true
        val tag = "fake-hashtag"
        val url = "https://$CURRENT_INSTANCE/search?tag=$tag"

        sut.openUri(url)

        verifySuspend {
            hashtagProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given user profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel())
        everySuspend { userProcessor.process(any()) } returns true
        val url = "https://$CURRENT_INSTANCE/profile/username"

        sut.openUri(url)

        verifySuspend {
            userProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given post display URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel())
        everySuspend { entryProcessor.process(any()) } returns true
        val url = "https://$CURRENT_INSTANCE/display/objectId"

        sut.openUri(url)

        verifySuspend {
            entryProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    companion object {
        private const val CURRENT_INSTANCE = "fake-instance.com"
        private const val OTHER_INSTANCE = "other-instance.com"
    }
}
