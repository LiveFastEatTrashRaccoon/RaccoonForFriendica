package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DefaultAppInfoRepository : AppInfoRepository {
    private val _appInfo = MutableStateFlow(getInfo())

    override val appInfo: StateFlow<AppInfo> = _appInfo.asStateFlow()

    private fun getInfo(): AppInfo {
        // TODO(jvm): implement
        return AppInfo(
            versionCode = "",
            isDebug = true,
        )
    }
}
