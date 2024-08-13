package com.livefast.eattrash.raccoonforfriendica.core.utils.validation

import platform.Foundation.NSURL

actual fun String.isValidUrl(): Boolean = NSURL.URLWithString(this) != null
