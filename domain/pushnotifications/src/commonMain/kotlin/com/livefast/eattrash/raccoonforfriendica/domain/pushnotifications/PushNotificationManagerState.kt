package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface PushNotificationManagerState {
    data object Unsupported : PushNotificationManagerState

    data object Initializing : PushNotificationManagerState

    data object NoDistributors : PushNotificationManagerState

    data object NoDistributorSelected : PushNotificationManagerState

    data object Idle : PushNotificationManagerState

    data object Enabled : PushNotificationManagerState
}

@Composable
fun PushNotificationManagerState.toReadableName(): String =
    when (this) {
        PushNotificationManagerState.Unsupported -> LocalStrings.current.settingsPushNotificationStateUnsupported
        PushNotificationManagerState.Initializing -> LocalStrings.current.settingsPushNotificationStateInitializing
        PushNotificationManagerState.NoDistributors -> LocalStrings.current.settingsPushNotificationStateNoDistributors
        PushNotificationManagerState.Enabled -> LocalStrings.current.settingsPushNotificationStateEnabled
        PushNotificationManagerState.Idle -> LocalStrings.current.settingsPushNotificationStateIdle
        PushNotificationManagerState.NoDistributorSelected -> LocalStrings.current.settingsPushNotificationStateNoDistributorSelected
    }
