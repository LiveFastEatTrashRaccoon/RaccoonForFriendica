package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import org.koin.core.annotation.Single

@Single
internal class DefaultExportUserListUseCase(private val userRepository: UserRepository) : ExportUserListUseCase {
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
            val response =
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
                }
            val list = response?.list.orEmpty()
            canFetchMore = response?.cursor != null && list.isNotEmpty()
            cursor = response?.cursor
            result += list
        }
        return result
    }
}
