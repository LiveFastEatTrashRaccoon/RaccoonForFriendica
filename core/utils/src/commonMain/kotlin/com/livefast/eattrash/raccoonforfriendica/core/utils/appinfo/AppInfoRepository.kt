package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
interface AppInfoRepository {
    val appInfo: StateFlow<AppInfo?>
}
