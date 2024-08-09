package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface UserSection {
    data object Posts : UserSection

    data object All : UserSection

    data object Pinned : UserSection

    data object Media : UserSection
}

fun UserSection.toInt(): Int =
    when (this) {
        UserSection.Posts -> 0
        UserSection.All -> 1
        UserSection.Pinned -> 2
        UserSection.Media -> 3
    }

fun Int.toAccountSection(): UserSection =
    when (this) {
        1 -> UserSection.All
        2 -> UserSection.Pinned
        3 -> UserSection.Media
        else -> UserSection.Posts
    }

@Composable
fun UserSection.toReadableName(): String =
    when (this) {
        UserSection.Posts -> LocalStrings.current.accountSectionPosts
        UserSection.All -> LocalStrings.current.accountSectionAll
        UserSection.Pinned -> LocalStrings.current.accountSectionPinned
        UserSection.Media -> LocalStrings.current.accountSectionMedia
    }
