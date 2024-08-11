package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface FavoritesType {
    data object Favorites : FavoritesType

    data object Bookmarks : FavoritesType
}

@Composable
fun FavoritesType.toReadableName() =
    when (this) {
        FavoritesType.Bookmarks -> LocalStrings.current.bookmarksTitle
        FavoritesType.Favorites -> LocalStrings.current.favoritesTitle
    }
