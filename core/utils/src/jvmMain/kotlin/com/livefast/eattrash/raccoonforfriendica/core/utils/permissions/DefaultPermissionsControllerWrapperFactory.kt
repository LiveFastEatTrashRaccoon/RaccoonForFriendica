package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

class DefaultPermissionsControllerWrapperFactory : PermissionsControllerWrapperFactory {
    override fun create(): PermissionControllerWrapper {
        return DefaultPermissionControllerWrapper()
    }
}
