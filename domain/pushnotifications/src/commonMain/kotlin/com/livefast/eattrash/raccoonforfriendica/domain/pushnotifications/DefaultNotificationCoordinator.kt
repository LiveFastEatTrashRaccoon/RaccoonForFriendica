package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.NotificationMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications.PullNotificationManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class DefaultNotificationCoordinator(
    private val settingsRepository: SettingsRepository,
    private val inboxManager: InboxManager,
    private val pullNotificationManager: PullNotificationManager,
    private val pushNotificationManager: PushNotificationManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NotificationCoordinator {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var monitorPullNotificationCheckIntervalJob: Job? = null
    private var monitorNotificationModeJob: Job? = null

    override suspend fun setupAnonymousUser() {
        stopMonitoringNotificationMode()
        stopMonitoringPullNotificationCheckInterval()
        pullNotificationManager.stop()
        inboxManager.clearUnreadCount()
    }

    override suspend fun setupLoggedUser() {
        pullNotificationManager.stop()
        inboxManager.refreshUnreadCount()
        startMonitoringNotificationMode()
    }

    private fun startMonitoringPullNotificationCheckInterval() {
        monitorPullNotificationCheckIntervalJob =
            settingsRepository.current
                .map {
                    it?.pullNotificationCheckInterval
                }.distinctUntilChanged()
                .onEach {
                    val minutes = it?.inWholeMinutes
                    if (minutes != null) {
                        pullNotificationManager.setPeriod(minutes)
                        pullNotificationManager.start()
                    } else {
                        pullNotificationManager.stop()
                    }
                }.launchIn(scope)
    }

    private fun stopMonitoringPullNotificationCheckInterval() {
        monitorPullNotificationCheckIntervalJob?.cancel()
        monitorPullNotificationCheckIntervalJob = null
    }

    private fun startMonitoringNotificationMode() {
        monitorNotificationModeJob =
            settingsRepository.current
                .map {
                    it?.notificationMode
                }.distinctUntilChanged()
                .onEach { mode ->
                    when (mode) {
                        NotificationMode.Pull -> {
                            startMonitoringPullNotificationCheckInterval()
                        }

                        NotificationMode.Push -> {
                            stopMonitoringPullNotificationCheckInterval()
                            pushNotificationManager.refreshState()
                            pushNotificationManager.startup()
                        }

                        else -> {
                            stopMonitoringNotificationMode()
                            stopMonitoringPullNotificationCheckInterval()
                        }
                    }
                }.launchIn(scope)
    }

    private fun stopMonitoringNotificationMode() {
        monitorNotificationModeJob?.cancel()
        monitorNotificationModeJob = null
    }
}
