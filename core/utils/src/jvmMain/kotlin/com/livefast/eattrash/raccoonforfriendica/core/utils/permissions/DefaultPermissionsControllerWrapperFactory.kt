package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

internal class DefaultPermissionsControllerWrapperFactory() : PermissionsControllerWrapperFactory {
    override fun create(): PermissionControllerWrapper {
        return DefaultPermissionControllerWrapper()
    }
}
