package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import android.util.Log

private const val TAG = "com.livefast.eattrash.raccoonforfriendica"

actual fun logDebug(message: String) {
    Log.d(TAG, message)
}
