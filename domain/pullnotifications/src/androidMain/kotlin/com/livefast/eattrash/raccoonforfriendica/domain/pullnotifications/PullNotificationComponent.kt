package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

import com.livefast.eattrash.raccoonforfriendica.core.l10n.Strings
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.LogFactory
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager

interface PullNotificationComponent {
    val inboxManager: InboxManager

    val strings: Strings

    val logFactory: LogFactory
}
