package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

interface PermissionControllerWrapper {
    suspend fun getPermissionState(permission: PermissionType): PermissionState

    suspend fun providePermission(permission: PermissionType)

    suspend fun openAppSettings()
}
