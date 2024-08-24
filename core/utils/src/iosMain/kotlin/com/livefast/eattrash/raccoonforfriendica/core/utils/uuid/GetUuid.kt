package com.livefast.eattrash.raccoonforfriendica.core.utils.uuid

import platform.Foundation.NSUUID

actual fun getUuid(): String = NSUUID.UUID().UUIDString
