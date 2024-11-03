package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications

interface NotificationCoordinator {
    suspend fun setupAnonymousUser()

    suspend fun setupLoggedUser()
}
