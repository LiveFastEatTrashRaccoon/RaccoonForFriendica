package com.livefast.eattrash.raccoonforfriendica.feature.profile.domain

import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManagerState

interface MyAccountCache {
    fun store(user: UserModel)

    fun store(paginationState: TimelinePaginationManagerState)

    fun store(section: UserSection)

    fun retrieveUser(): UserModel?

    fun retrievePaginationState(): TimelinePaginationManagerState?

    fun retrieveSection(): UserSection

    fun clear()
}
