package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

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

fun FavoritesType.toInt(): Int =
    when (this) {
        FavoritesType.Bookmarks -> 1
        FavoritesType.Favorites -> 0
    }

fun Int.toFavoritesType(): FavoritesType =
    when (this) {
        1 -> FavoritesType.Bookmarks
        else -> FavoritesType.Favorites
    }
