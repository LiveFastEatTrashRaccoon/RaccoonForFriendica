package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel

internal interface FetchUserUseCase {
    suspend operator fun invoke(url: String): UserModel?
}
