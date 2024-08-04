package com.github.akesiseli.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.github.akesiseli.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface TimelineType {
    data object All : TimelineType

    data object Subscriptions : TimelineType
}

@Composable
fun TimelineType.readableName(): String =
    when (this) {
        TimelineType.All -> LocalStrings.current.timelineAll
        TimelineType.Subscriptions -> LocalStrings.current.timelineSubscriptions
    }
