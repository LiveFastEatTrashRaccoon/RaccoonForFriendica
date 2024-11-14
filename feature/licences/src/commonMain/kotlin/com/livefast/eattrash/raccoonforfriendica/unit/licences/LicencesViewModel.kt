package com.livefast.eattrash.raccoonforfriendica.unit.licences

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.unit.licences.models.LicenceItem
import com.livefast.eattrash.raccoonforfriendica.unit.licences.models.LicenceItemType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LicencesViewModel(
    private val settingsRepository: SettingsRepository,
) : DefaultMviModel<LicencesMviModel.Intent, LicencesMviModel.State, LicencesMviModel.Effect>(
        initialState = LicencesMviModel.State(),
    ),
    LicencesMviModel {
    init {
        screenModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                                settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            populate()
        }
    }

    private suspend fun populate() {
        updateState {
            it.copy(
                items =
                    buildList {
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Android Jetpack",
                                subtitle = "A suite of libraries, tools, and guidance to help developers write high-quality apps easier",
                                url = LicenceUrls.ANDROIDX,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Resource,
                                title = "Atkinson Hyperlegible, Exo, Noto Sans",
                                subtitle = "Fonts used in the app are released under the Open Font Library (OFL)",
                                url = LicenceUrls.OFL,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Bouncy Castle",
                                subtitle =
                                    "The Bouncy Castle Crypto package is a Java implementation of cryptographic algorithms",
                                url = LicenceUrls.BOUNCY_CASTLE,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Calf webview",
                                subtitle =
                                    "Calf is a library that allows you to easily create adaptive UIs and access platform specific APIs from your Compose Multiplatform apps",
                                url = LicenceUrls.CALF,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Coil",
                                subtitle = "An image loading library for Android",
                                url = LicenceUrls.COIL,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Compose ColorPicker",
                                subtitle = "Kotlin Multiplatform color picker library",
                                url = LicenceUrls.COMPOSE_COLORPICKER,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Compose Multiplatform Media Player",
                                subtitle =
                                    "Compose Multiplatform Media Player is a powerful media player library designed for Compose Multiplatform projects",
                                url = LicenceUrls.COMPOSE_MULTIPLATFORM_MEDIA_PLAYER,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Connectivity",
                                subtitle =
                                    "Connectivity provides network monitoring capabilities for multiplatform projects",
                                url = LicenceUrls.CONNECTIVITY,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Resource,
                                title = "Friendica logo",
                                subtitle = "Logo of the Friendica project",
                                url = LicenceUrls.FRIENDICA,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Koin",
                                subtitle = "A pragmatic lightweight dependency injection framework",
                                url = LicenceUrls.KOIN,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Ksoup",
                                subtitle = "Kotlin Multiplatform HTML & XML Parser",
                                url = LicenceUrls.KSOUP,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Ktor",
                                subtitle = "An asynchronous framework for creating microservices, web applications and more",
                                url = LicenceUrls.KTOR,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Ktorfit",
                                subtitle = "A HTTP client/Kotlin Symbol Processor for Kotlin Multiplatform",
                                url = LicenceUrls.KTORFIT,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Lyricist",
                                subtitle = "The missing I18N and L10N multiplatform library for Jetpack Compose",
                                url = LicenceUrls.LYRICIST,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Resource,
                                title = "Mastodon logo",
                                subtitle = "Logo of the Mastodon project",
                                url = LicenceUrls.MASTODON,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Resource,
                                title = "Material Design Icons",
                                subtitle = "A set of icons by Google",
                                url = LicenceUrls.MATERIAL_ICONS,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "MaterialKolor",
                                subtitle = "A Compose Multiplatform library for creating dynamic Material Design 3 color palettes",
                                url = LicenceUrls.MATERIAL_KOLOR,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Multiplatform Settings",
                                subtitle = "A Kotlin library for Multiplatform apps, so that common code can persist key-value data",
                                url = LicenceUrls.SETTINGS,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Sentry",
                                subtitle = "Sentry SDK for Kotlin Multiplatform",
                                url = LicenceUrls.SENTRY,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "UnifiedPush",
                                subtitle =
                                    "This is a library that can be used by an end user application to receive notifications from any unified push provider",
                                url = LicenceUrls.UNIFIED_PUSH,
                            )
                        this +=
                            LicenceItem(
                                type = LicenceItemType.Library,
                                title = "Voyager",
                                subtitle = "A multiplatform navigation library built for, and seamlessly integrated with, Jetpack Compose",
                                url = LicenceUrls.VOYAGER,
                            )
                    },
            )
        }
    }
}
