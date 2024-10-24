package com.livefast.eattrash.raccoonforfriendica.feaure.search.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface SearchSection {
    data object Posts : SearchSection

    data object Users : SearchSection

    data object Hashtags : SearchSection
}

@Composable
fun SearchSection.toReadableName(): String =
    when (this) {
        SearchSection.Posts -> LocalStrings.current.accountSectionPosts
        SearchSection.Users -> LocalStrings.current.searchSectionUsers
        SearchSection.Hashtags -> LocalStrings.current.exploreSectionHashtags
    }
