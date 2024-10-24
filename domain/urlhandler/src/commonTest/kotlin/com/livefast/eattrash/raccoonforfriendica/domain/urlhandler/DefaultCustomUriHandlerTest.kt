package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.ExternalUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.FriendicaUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.GuppeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.HashtagProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.LemmyProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.MastodonUserProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.PeertubeProcessor
import com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor.PixelfedProcessor
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
    private val friendicaUserProcessor =
        mock<FriendicaUserProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val externalUserProcessor =
        mock<ExternalUserProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val mastodonUserProcessor =
        mock<MastodonUserProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val lemmyProcessor =
        mock<LemmyProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val guppeProcessor =
        mock<GuppeProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val peertubeProcessor =
        mock<PeertubeProcessor> {
            everySuspend { process(any()) } returns false
        }
    private val pixelfedProcessor =
        mock<PixelfedProcessor> {
            everySuspend { process(any()) } returns false
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sut =
        DefaultCustomUriHandler(
            defaultHandler = defaultHandler,
            customTabsHelper = customTabsHelper,
            settingsRepository = settingsRepository,
            hashtagProcessor = hashtagProcessor,
            friendicaUserProcessor = friendicaUserProcessor,
            externalUserProcessor = externalUserProcessor,
            mastodonUserProcessor = mastodonUserProcessor,
            lemmyProcessor = lemmyProcessor,
            guppeProcessor = guppeProcessor,
            peertubeProcessor = peertubeProcessor,
            pixelfedProcessor = pixelfedProcessor,
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
    fun `given hashtag URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
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
    fun `given Friendica profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { friendicaUserProcessor.process(any()) } returns true
        val url = "https://$CURRENT_INSTANCE/profile/username"

        sut.openUri(url)

        verifySuspend {
            friendicaUserProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given external profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { externalUserProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/users/username"

        sut.openUri(url)

        verifySuspend {
            externalUserProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Mastodon profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { mastodonUserProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/@username"

        sut.openUri(url)

        verifySuspend {
            mastodonUserProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Lemmy user URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { lemmyProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/u/username"

        sut.openUri(url)

        verifySuspend {
            lemmyProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Lemmy community URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { lemmyProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/c/username"

        sut.openUri(url)

        verifySuspend {
            lemmyProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Guppe group URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { guppeProcessor.process(any()) } returns true
        val url = "https://a.gup.pe/u/username"

        sut.openUri(url)

        verifySuspend {
            guppeProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Peertube channel URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { peertubeProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/video-channels/username"

        sut.openUri(url)

        verifySuspend {
            peertubeProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Peertube user URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { peertubeProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/accounts/username"

        sut.openUri(url)

        verifySuspend {
            peertubeProcessor.process(url)
        }
        verify(mode = VerifyMode.not) {
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Pixelfed user URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        everySuspend { pixelfedProcessor.process(any()) } returns true
        val url = "https://$OTHER_INSTANCE/username"

        sut.openUri(url)

        verifySuspend {
            pixelfedProcessor.process(url)
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
