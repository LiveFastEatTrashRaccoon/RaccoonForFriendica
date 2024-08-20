package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

data class AppInfo(
    val versionCode: String,
    val isDebug: Boolean,
)

@Stable
interface AppInfoRepository {
    val appInfo: StateFlow<AppInfo?>
}
