package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

import androidx.compose.runtime.Composable
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory

@Composable
actual fun rememberPermissionsControllerWrapperFactory(): PermissionsControllerWrapperFactory {
    val libFactory = rememberPermissionsControllerFactory()
    return DefaultPermissionsControllerWrapperFactory(libFactory)
}

@Composable
actual fun PermissionControllerWrapperBindEffect(controller: PermissionControllerWrapper) {
    BindEffect(permissionsController = (controller as InnerPermissionControllerWrapper).controller)
}
