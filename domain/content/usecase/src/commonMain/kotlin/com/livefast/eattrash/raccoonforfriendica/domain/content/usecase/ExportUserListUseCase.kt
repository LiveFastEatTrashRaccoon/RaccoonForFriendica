package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

sealed interface ExportUserSpecification {
    data class Follower(val userId: String) : ExportUserSpecification

    data class Following(val userId: String) : ExportUserSpecification
}

interface ExportUserListUseCase {
    suspend operator fun invoke(specification: ExportUserSpecification): String
}
