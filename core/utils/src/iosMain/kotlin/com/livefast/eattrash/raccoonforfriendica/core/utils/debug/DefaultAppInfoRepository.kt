package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSBundle
import kotlin.experimental.ExperimentalNativeApi

class DefaultAppInfoRepository : AppInfoRepository {
    override val appInfo = MutableStateFlow(getInfo())

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
