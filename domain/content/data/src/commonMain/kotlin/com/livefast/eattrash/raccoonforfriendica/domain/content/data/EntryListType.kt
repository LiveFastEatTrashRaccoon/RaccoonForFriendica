package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface EntryListType {
    data object Favorites : EntryListType

    data object Bookmarks : EntryListType

    data object Quoting : EntryListType
}
