package com.livefast.eattrash.raccoonforfriendica.core.utils.uuid

import platform.Foundation.NSUUID

fun getUuid(): String = NSUUID.UUID().UUIDString
