package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSBundle
import kotlin.experimental.ExperimentalNativeApi

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultAppInfoRepository : AppInfoRepository {
    override val appInfo: StateFlow<AppInfo?> field = MutableStateFlow(getInfo())

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
