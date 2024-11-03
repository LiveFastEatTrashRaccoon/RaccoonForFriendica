package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.NotificationMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class DefaultNotificationCoordinatorTest {
    private val settingsState = MutableStateFlow(SettingsModel())
    private val settingsRepository =
        mock<SettingsRepository> {
            every { current } returns settingsState
        }
    private val inboxManager = mock<InboxManager>(mode = MockMode.autoUnit)
    private val pullNotificationManager = mock<PullNotificationManager>(mode = MockMode.autoUnit)
    private val pushNotificationManager = mock<PushNotificationManager>(mode = MockMode.autoUnit)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sut =
        DefaultNotificationCoordinator(
            settingsRepository = settingsRepository,
            inboxManager = inboxManager,
            pullNotificationManager = pullNotificationManager,
            pushNotificationManager = pushNotificationManager,
            dispatcher = UnconfinedTestDispatcher(),
        )

    @Test
    fun `when setupAnonymousUser then interactions are as expected`() =
        runTest {
            sut.setupAnonymousUser()

            verifySuspend {
                pullNotificationManager.stop()
                inboxManager.clearUnreadCount()
            }
        }

    @Test
    fun `given no logged user when mode changes to Pull then interactions are as expected`() =
        runTest {
            sut.setupAnonymousUser()

            settingsState.update {
                SettingsModel(
                    notificationMode = NotificationMode.Pull,
                    pullNotificationCheckInterval = 10.minutes,
                )
            }

            verify(mode = VerifyMode.not) {
                pullNotificationManager.setPeriod(any())
                pullNotificationManager.start()
            }
        }

    @Test
    fun `given no logged user when mode changes to Push then interactions are as expected`() =
        runTest {
            sut.setupAnonymousUser()

            settingsState.update { SettingsModel(notificationMode = NotificationMode.Push) }

            verifySuspend(mode = VerifyMode.not) {
                pushNotificationManager.refreshState()
                pushNotificationManager.startup()
            }
        }

    @Test
    fun `when setupLoggedUser then interactions are as expected`() =
        runTest {
            sut.setupLoggedUser()

            verifySuspend {
                pullNotificationManager.stop()
                inboxManager.refreshUnreadCount()
            }
        }

    @Test
    fun `given logged user when mode changes to Pull then interactions are as expected`() =
        runTest {
            sut.setupLoggedUser()

            settingsState.update {
                SettingsModel(
                    notificationMode = NotificationMode.Pull,
                    pullNotificationCheckInterval = 10.minutes,
                )
            }

            verify {
                pullNotificationManager.setPeriod(minutes = 10)
                pullNotificationManager.start()
            }
        }

    @Test
    fun `given logged user when mode changes to Push then interactions are as expected`() =
        runTest {
            sut.setupLoggedUser()

            settingsState.update { SettingsModel(notificationMode = NotificationMode.Push) }

            verifySuspend {
                pushNotificationManager.refreshState()
                pushNotificationManager.startup()
            }
        }
}
