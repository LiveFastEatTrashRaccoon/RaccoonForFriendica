package com.livefast.eattrash.raccoonforfriendica.core.utils.validation

import android.util.Patterns

actual fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()
