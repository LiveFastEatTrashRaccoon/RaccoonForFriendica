package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class DefaultAppInfoRepository(
    private val context: Context,
) : AppInfoRepository {
    private val _appInfo = MutableStateFlow(geInfo())
    override val appInfo: StateFlow<AppInfo?> = _appInfo

    private fun geInfo(): AppInfo? =
        runCatching {
            with(context) {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                AppInfo(
                    versionCode =
                        buildString {
                            append(packageInfo.versionName)
                            append(" (")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                append(packageInfo.longVersionCode)
                            } else {
                                append(packageInfo.versionCode)
                            }
                            append(")")
                        },
                    isDebug = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
                )
            }
        }.getOrNull()
}
