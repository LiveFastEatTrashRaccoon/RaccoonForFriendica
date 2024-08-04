package com.github.akesiseli.raccoonforfriendica.core.utils.debug

import android.util.Log

private const val TAG = "com.github.akesiseli.raccoonforfriendica"

actual fun logDebug(message: String) {
    Log.d(TAG, message)
}
