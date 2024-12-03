package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.coordinator

interface NotificationCoordinator {
    suspend fun setupAnonymousUser()

    suspend fun setupLoggedUser()
}
