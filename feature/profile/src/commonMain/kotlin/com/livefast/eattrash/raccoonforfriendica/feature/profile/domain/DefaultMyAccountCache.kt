package com.livefast.eattrash.raccoonforfriendica.feature.profile.domain

import com.livefast.eattrash.raccoonforfriendica.core.commonui.content.UserSection
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.TimelinePaginationManagerState

internal class DefaultMyAccountCache : MyAccountCache {
    private var cachedUser: UserModel? = null
    private var cachedPaginationState: TimelinePaginationManagerState? = null
    private var cachedSection: UserSection = UserSection.Posts

    override fun store(user: UserModel) {
        cachedUser = user
    }

    override fun store(paginationState: TimelinePaginationManagerState) {
        cachedPaginationState = paginationState
    }

    override fun store(section: UserSection) {
        cachedSection = section
    }

    override fun retrieveUser(): UserModel? = cachedUser

    override fun retrievePaginationState(): TimelinePaginationManagerState? = cachedPaginationState

    override fun retrieveSection(): UserSection = cachedSection

    override fun clear() {
        cachedUser = null
        cachedPaginationState = null
    }
}
