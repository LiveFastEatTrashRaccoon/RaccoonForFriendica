package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun ByteArray.toComposeImageBitmap(): ImageBitmap = BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
