package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPermissionsControllerWrapperFactory(): PermissionsControllerWrapperFactory = remember {
    DefaultPermissionsControllerWrapperFactory()
}

@Composable
actual fun PermissionControllerWrapperBindEffect(controller: PermissionControllerWrapper) {
    // no-op
}
