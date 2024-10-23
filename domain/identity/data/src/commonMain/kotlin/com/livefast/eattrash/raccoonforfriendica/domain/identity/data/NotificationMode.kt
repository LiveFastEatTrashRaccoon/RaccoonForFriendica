package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface NotificationMode {
    data object Pull : NotificationMode

    data object Push : NotificationMode

    data object Disabled : NotificationMode
}

@Composable
fun NotificationMode.toReadableName(): String =
    when (this) {
        NotificationMode.Disabled -> LocalStrings.current.settingsNotificationModeDisabled
        NotificationMode.Pull -> LocalStrings.current.settingsNotificationModePull
        NotificationMode.Push -> LocalStrings.current.settingsNotificationModePush
    }
