package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import androidx.compose.ui.platform.UriHandler
import com.livefast.eattrash.raccoonforfriendica.core.navigation.DetailOpener
import com.livefast.eattrash.raccoonforfriendica.core.utils.url.CustomTabsHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.UrlOpeningMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test

class DefaultCustomUriHandlerTest {
    private val defaultHandler = mock<UriHandler>(mode = MockMode.autoUnit)
    private val apiConfigurationRepository =
        mock<ApiConfigurationRepository> {
            every { node } returns MutableStateFlow(CURRENT_INSTANCE)
        }
    private val userRepository = mock<UserRepository>()
    private val detailOpener = mock<DetailOpener>(mode = MockMode.autoUnit)
    private val customTabsHelper =
        mock<CustomTabsHelper>(mode = MockMode.autoUnit) {
            every { isSupported } returns false
        }
    private val settingsRepository =
        mock<SettingsRepository> {
            every { current } returns MutableStateFlow(SettingsModel())
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sut =
        DefaultCustomUriHandler(
            defaultHandler = defaultHandler,
            apiConfigurationRepository = apiConfigurationRepository,
            userRepository = userRepository,
            detailOpener = detailOpener,
            customTabsHelper = customTabsHelper,
            settingsRepository = settingsRepository,
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
        val tag = "fake-hashtag"
        val url = "https://$CURRENT_INSTANCE/search?tag=$tag"

        sut.openUri(url)

        verify {
            detailOpener.openHashtag(tag)
        }
    }

    @Test
    fun `given local profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "username"
        val user = UserModel(id = "user-id", username = name)
        everySuspend { userRepository.getByHandle(any()) } returns user
        val url = "https://$CURRENT_INSTANCE/profile/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$CURRENT_INSTANCE")
            detailOpener.openUserDetail(user)
        }
    }

    @Test
    fun `given nonexistent local profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "username"
        everySuspend { userRepository.getByHandle(any()) } returns null
        val url = "https://$CURRENT_INSTANCE/profile/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$CURRENT_INSTANCE")
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given external profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "username"
        val user = UserModel(id = "user-id", username = name)
        everySuspend { userRepository.getByHandle(any()) } returns user
        val url = "https://$OTHER_INSTANCE/users/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$OTHER_INSTANCE")
            detailOpener.openUserDetail(user)
        }
    }

    @Test
    fun `given nonexistent external profile URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "username"
        everySuspend { userRepository.getByHandle(any()) } returns null
        val url = "https://$OTHER_INSTANCE/users/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$OTHER_INSTANCE")
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Lemmy user URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "username"
        val user = UserModel(id = "user-id", username = name)
        everySuspend { userRepository.getByHandle(any()) } returns user
        val url = "https://$OTHER_INSTANCE/u/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$OTHER_INSTANCE")
            detailOpener.openUserDetail(user)
        }
    }

    @Test
    fun `given nonexistent Lemmy user URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "username"
        everySuspend { userRepository.getByHandle(any()) } returns null
        val url = "https://$OTHER_INSTANCE/u/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$OTHER_INSTANCE")
            defaultHandler.openUri(url)
        }
    }

    @Test
    fun `given Lemmy community URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "community"
        val user = UserModel(id = "user-id", username = name)
        everySuspend { userRepository.getByHandle(any()) } returns user
        val url = "https://$OTHER_INSTANCE/c/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$OTHER_INSTANCE")
            detailOpener.openUserDetail(user)
        }
    }

    @Test
    fun `given nonexistent Lemmy community URL when openUri then interactions are as expected`() {
        every {
            settingsRepository.current
        } returns MutableStateFlow(SettingsModel(urlOpeningMode = UrlOpeningMode.CustomTabs))
        val name = "community"
        everySuspend { userRepository.getByHandle(any()) } returns null
        val url = "https://$OTHER_INSTANCE/c/$name"

        sut.openUri(url)

        verifySuspend {
            userRepository.getByHandle("$name@$OTHER_INSTANCE")
            defaultHandler.openUri(url)
        }
    }

    companion object {
        private const val CURRENT_INSTANCE = "fake-instance.com"
        private const val OTHER_INSTANCE = "other-instance.com"
    }
}
