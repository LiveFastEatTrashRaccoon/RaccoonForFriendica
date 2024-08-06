package com.livefast.eattrash.feature.accountdetail

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface AccountSection {
    data object Posts : AccountSection

    data object All : AccountSection

    data object Pinned : AccountSection

    data object Media : AccountSection
}

fun AccountSection.toInt(): Int =
    when (this) {
        AccountSection.Posts -> 0
        AccountSection.All -> 1
        AccountSection.Pinned -> 2
        AccountSection.Media -> 3
    }

fun Int.toAccountSection(): AccountSection =
    when (this) {
        1 -> AccountSection.All
        2 -> AccountSection.Pinned
        3 -> AccountSection.Media
        else -> AccountSection.Posts
    }

@Composable
fun AccountSection.toReadableName(): String =
    when (this) {
        AccountSection.Posts -> LocalStrings.current.accountSectionPosts
        AccountSection.All -> LocalStrings.current.accountSectionAll
        AccountSection.Pinned -> LocalStrings.current.accountSectionPinned
        AccountSection.Media -> LocalStrings.current.accountSectionMedia
    }
