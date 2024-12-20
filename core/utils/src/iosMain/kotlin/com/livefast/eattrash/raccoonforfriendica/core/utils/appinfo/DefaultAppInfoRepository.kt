package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSBundle
import kotlin.experimental.ExperimentalNativeApi

internal class DefaultAppInfoRepository : AppInfoRepository {
    private val _appInfo = MutableStateFlow(getInfo())
    override val appInfo: StateFlow<AppInfo?> = _appInfo

    @OptIn(ExperimentalNativeApi::class)
    private fun getInfo(): AppInfo {
        val versionCode =
            buildString {
                val dict = NSBundle.mainBundle.infoDictionary
                val buildNumber = dict?.get("CFBundleVersion") as? String ?: ""
                val versionName = dict?.get("CFBundleShortVersionString") as? String ?: ""
                if (versionName.isNotEmpty()) {
                    append(versionName)
                }
                if (buildNumber.isNotEmpty()) {
                    append(" (")
                    append(buildNumber)
                    append(")")
                }
            }
        val isDebug = Platform.isDebugBinary
        return AppInfo(
            versionCode = versionCode,
            isDebug = isDebug,
        )
    }
}
