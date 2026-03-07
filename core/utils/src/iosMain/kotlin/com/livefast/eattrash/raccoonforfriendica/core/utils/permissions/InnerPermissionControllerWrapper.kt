package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

import dev.icerock.moko.permissions.PermissionsController

internal interface InnerPermissionControllerWrapper {
    val controller: PermissionsController
}
