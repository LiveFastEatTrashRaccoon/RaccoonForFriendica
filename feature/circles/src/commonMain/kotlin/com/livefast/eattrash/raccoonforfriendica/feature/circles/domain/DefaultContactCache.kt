package com.livefast.eattrash.raccoonforfriendica.feature.circles.domain

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.pagination.UserPaginationSpecification
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DefaultContactCache(
    private val identityRepository: IdentityRepository,
    private val userPaginationManager: UserPaginationManager,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ContactCache {
    private var userCache = listOf<UserModel>()
    private val scope = CoroutineScope(SupervisorJob() + coroutineDispatcher)

    override fun refresh() {
        scope.launch {
            val currentUserId = identityRepository.currentUser.value?.id ?: return@launch
            userPaginationManager.reset(UserPaginationSpecification.Following(currentUserId))
            while (userPaginationManager.canFetchMore) {
                userCache = userPaginationManager.loadNextPage()
            }
        }
    }

    override fun getContacts(excludeIds: List<String>): List<UserModel> =
        userCache.filter {
            it.id !in excludeIds
        }.map { it.copy(relationshipStatus = null) }
}
