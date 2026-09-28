package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import kotlinx.coroutines.flow.StateFlow

interface AppInfoRepository {
    val appInfo: StateFlow<AppInfo?>
}
