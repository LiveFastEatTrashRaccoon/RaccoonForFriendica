package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import dev.icerock.moko.permissions.DeniedAlwaysException as LibDeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException as LibDeniedException
import dev.icerock.moko.permissions.PermissionState as LibPermissionState
import dev.icerock.moko.permissions.RequestCanceledException as LibDRequestCanceledException

internal class DefaultPermissionControllerWrapper(override val controller: PermissionsController) :
    PermissionControllerWrapper, InnerPermissionControllerWrapper {

    override suspend fun getPermissionState(permission: PermissionType): PermissionState {
        val libState = when (permission) {
            PermissionType.PushNotification -> controller.getPermissionState(Permission.REMOTE_NOTIFICATION)
        }
        return when (libState) {
            LibPermissionState.Granted -> PermissionState.Granted
            LibPermissionState.Denied -> PermissionState.Denied
            LibPermissionState.NotDetermined -> PermissionState.NotDetermined
            LibPermissionState.NotGranted -> PermissionState.NotGranted
            LibPermissionState.DeniedAlways -> PermissionState.DeniedAlways
        }
    }

    override suspend fun providePermission(permission: PermissionType) {
        val libPermission = when (permission) {
            PermissionType.PushNotification -> Permission.REMOTE_NOTIFICATION
        }
        try {
            controller.providePermission(libPermission)
        } catch (_: LibDeniedAlwaysException) {
            throw DeniedAlwaysException()
        } catch (_: LibDeniedException) {
            throw DeniedException()
        } catch (_: LibDRequestCanceledException) {
            throw RequestCanceledException()
        }
    }

    override suspend fun openAppSettings() {
        controller.openAppSettings()
    }
}
