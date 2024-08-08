package com.livefast.eattrash.raccoonforfriendica.core.utils.compose

import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.Modifier

actual fun Modifier.safeImePadding(): Modifier = then(Modifier.imePadding())
