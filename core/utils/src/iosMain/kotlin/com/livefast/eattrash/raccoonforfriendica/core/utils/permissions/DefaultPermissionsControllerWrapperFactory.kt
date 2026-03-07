package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

import dev.icerock.moko.permissions.compose.PermissionsControllerFactory

internal class DefaultPermissionsControllerWrapperFactory(private val factory: PermissionsControllerFactory) : PermissionsControllerWrapperFactory {
    override fun create(): PermissionControllerWrapper {
        val controller = factory.createPermissionsController()
        return DefaultPermissionControllerWrapper(controller)
    }
}
