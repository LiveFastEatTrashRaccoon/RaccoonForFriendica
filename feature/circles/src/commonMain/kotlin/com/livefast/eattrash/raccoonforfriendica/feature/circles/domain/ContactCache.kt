package com.livefast.eattrash.raccoonforfriendica.feature.circles.domain

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

interface ContactCache {
    fun refresh()

    fun getContacts(excludeIds: List<String>): List<UserModel>
}
