package com.livefast.eattrash.raccoonforfriendica.core.utils.imageload

import androidx.compose.ui.graphics.ImageBitmap

expect fun ByteArray.toComposeImageBitmap(): ImageBitmap

expect fun IntArray.toComposeImageBitmap(
    width: Int,
    height: Int,
): ImageBitmap
