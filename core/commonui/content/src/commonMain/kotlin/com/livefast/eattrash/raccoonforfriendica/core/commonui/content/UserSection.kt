package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface UserSection {
    data object Posts : UserSection

    data object All : UserSection

    data object Pinned : UserSection

    data object Media : UserSection
}

@Composable
fun UserSection.toReadableName(): String =
    when (this) {
        UserSection.Posts -> LocalStrings.current.accountSectionPosts
        UserSection.All -> LocalStrings.current.accountSectionAll
        UserSection.Pinned -> LocalStrings.current.accountSectionPinned
        UserSection.Media -> LocalStrings.current.accountSectionMedia
    }
