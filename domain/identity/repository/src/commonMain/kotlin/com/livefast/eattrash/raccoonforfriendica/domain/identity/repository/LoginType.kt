package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

sealed interface LoginType {
    data object Friendica : LoginType

    data object Mastodon : LoginType

    data object Other : LoginType
}

fun LoginType.toInt(): Int = when (this) {
    LoginType.Friendica -> 0
    LoginType.Mastodon -> 1
    LoginType.Other -> 2
}

fun Int.toLoginType(): LoginType = when (this) {
    2 -> LoginType.Other
    1 -> LoginType.Mastodon
    else -> LoginType.Friendica
}
