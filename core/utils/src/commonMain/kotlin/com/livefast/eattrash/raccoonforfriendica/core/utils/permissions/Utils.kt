package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

import androidx.compose.runtime.Composable

@Composable
expect fun rememberPermissionsControllerWrapperFactory(): PermissionsControllerWrapperFactory

@Composable
expect fun PermissionControllerWrapperBindEffect(controller: PermissionControllerWrapper)
