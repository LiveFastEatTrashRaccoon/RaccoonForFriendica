package com.livefast.eattrash.raccoonforfriendica.feaure.search.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface SearchSection {
    data object Posts : SearchSection

    data object Hashtags : SearchSection

    data object Users : SearchSection
}

fun SearchSection.toInt(): Int =
    when (this) {
        SearchSection.Hashtags -> 0
        SearchSection.Posts -> 1
        SearchSection.Users -> 2
    }

fun Int.toSearchSection(): SearchSection =
    when (this) {
        0 -> SearchSection.Hashtags
        2 -> SearchSection.Users
        else -> SearchSection.Posts
    }

@Composable
fun SearchSection.toReadableName(): String =
    when (this) {
        SearchSection.Posts -> LocalStrings.current.accountSectionPosts
        SearchSection.Hashtags -> LocalStrings.current.exploreSectionHashtags
        SearchSection.Users -> LocalStrings.current.searchSectionUsers
    }
