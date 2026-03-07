package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

internal class DefaultPermissionControllerWrapper() : PermissionControllerWrapper {

    override suspend fun getPermissionState(permission: PermissionType): PermissionState {
        return PermissionState.NotDetermined
    }

    override suspend fun providePermission(permission: PermissionType) {
        // no-op
    }

    override suspend fun openAppSettings() {
        // no-op
    }
}
