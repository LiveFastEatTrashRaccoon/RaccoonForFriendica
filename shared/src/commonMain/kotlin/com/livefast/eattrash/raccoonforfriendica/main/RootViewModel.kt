package com.livefast.eattrash.raccoonforfriendica.main

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.CrashReportManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.ActiveAccountMonitor
import com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.SetupAccountUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class RootViewModel(
    private val settingsRepository: SettingsRepository,
    private val themeRepository: ThemeRepository,
    private val l10nManager: L10nManager,
    private val activeAccountMonitor: ActiveAccountMonitor,
    private val setupAccountUseCase: SetupAccountUseCase,
    private val crashReportManager: CrashReportManager,
) : ViewModel(),
    MviModelDelegate<RootMviModel.Intent, RootMviModel.UiState, RootMviModel.Effect> by DefaultMviModelDelegate(
        initialState = RootMviModel.UiState(),
    ),
    RootMviModel {
        private val mutex = Mutex()
        private var isInitialized = false

        init {
            // initialize crash reporting as soon as possible
            crashReportManager.initialize()

            viewModelScope.launch {
                activeAccountMonitor.start()
                setupAccountUseCase()

                launch {
                    // set a timeout on the initialization
                    delay(1.5.seconds)
                    finishInitialization()
                }

                settingsRepository.current.onEach { settings ->
                    updateState { it.copy(currentSettings = settings) }

                    if (settings != null) {
                        l10nManager.changeLanguage(settings.lang)
                        themeRepository.changeTheme(settings.theme)
                        themeRepository.changeCommentBarTheme(settings.commentBarTheme)
                        themeRepository.changeFontFamily(settings.fontFamily)
                        themeRepository.changeFontScale(settings.fontScale)
                        themeRepository.changeCustomSeedColor(
                            color = settings.customSeedColor?.let { c -> Color(color = c) },
                        )
                        finishInitialization()
                    }
                }.launchIn(this)
            }
        }

    override fun reduce(intent: RootMviModel.Intent)  = Unit

    private suspend fun finishInitialization() = mutex.withLock {
        if (!isInitialized) {
            isInitialized = true
            emitEffect(RootMviModel.Effect.InitializationFinished)
        }
    }
}
