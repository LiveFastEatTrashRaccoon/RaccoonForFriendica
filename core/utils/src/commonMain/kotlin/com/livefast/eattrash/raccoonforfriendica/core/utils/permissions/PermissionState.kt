package com.livefast.eattrash.raccoonforfriendica.core.utils.permissions

enum class PermissionState {
    Granted,
    NotGranted,
    Denied,
    DeniedAlways,
    NotDetermined,
}

enum class PermissionType {
    PushNotification
}

class DeniedAlwaysException : Exception()

class DeniedException : Exception()

class RequestCanceledException : Exception()
