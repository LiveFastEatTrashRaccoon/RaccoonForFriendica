package com.livefast.eattrash.raccoonforfriendica.core.navigation

interface DetailOpener {
    fun openUserDetail(id: String)

    fun openEntryDetail(id: String)

    fun openSettings()

    fun openLogin()

    fun openHashtag(tag: String)
}
