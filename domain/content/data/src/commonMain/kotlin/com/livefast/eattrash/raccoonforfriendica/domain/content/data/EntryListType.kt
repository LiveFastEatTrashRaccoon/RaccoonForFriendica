package com.livefast.eattrash.raccoonforfriendica.domain.content.data

sealed interface EntryListType {
    data object Favorites : EntryListType

    data object Bookmarks : EntryListType

    data class Quoting(val entryId: String, val otherInstance: String? = null) : EntryListType
}
