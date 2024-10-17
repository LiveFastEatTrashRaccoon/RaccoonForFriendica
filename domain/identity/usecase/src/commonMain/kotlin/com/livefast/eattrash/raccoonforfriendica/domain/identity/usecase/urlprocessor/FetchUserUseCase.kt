package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.urlprocessor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

internal interface FetchUserUseCase {
    suspend operator fun invoke(handle: String): UserModel?
}
