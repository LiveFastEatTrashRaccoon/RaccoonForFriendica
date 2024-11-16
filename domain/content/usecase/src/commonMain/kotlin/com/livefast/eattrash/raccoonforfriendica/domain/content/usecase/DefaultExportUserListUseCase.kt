package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository

internal class DefaultExportUserListUseCase(
    private val userRepository: UserRepository,
) : ExportUserListUseCase {
    override suspend fun invoke(specification: ExportUserSpecification): String {
        val users = retrieveUsers(specification)
        return users.mapNotNull { it.toExportData() }.joinToString("\n")
    }

    private fun UserModel.toExportData(): String? = handle

    private suspend fun retrieveUsers(specification: ExportUserSpecification): List<UserModel> {
        var cursor: String? = null
        var canFetchMore = true
        val result = mutableListOf<UserModel>()
        while (canFetchMore) {
            val list =
                when (specification) {
                    is ExportUserSpecification.Follower ->
                        userRepository.getFollowers(
                            id = specification.userId,
                            pageCursor = cursor,
                        )

                    is ExportUserSpecification.Following ->
                        userRepository.getFollowing(
                            id = specification.userId,
                            pageCursor = cursor,
                        )
                }.orEmpty()
            canFetchMore = list.isNotEmpty()
            cursor = list.lastOrNull()?.id
            result += list
        }
        return result
    }
}
