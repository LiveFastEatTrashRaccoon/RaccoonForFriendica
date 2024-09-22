package com.livefast.eattrash.raccoonforfriendica.core.utils.validation

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z].*@.+\\..+"
    return emailRegex.toRegex().matches(this)
}
