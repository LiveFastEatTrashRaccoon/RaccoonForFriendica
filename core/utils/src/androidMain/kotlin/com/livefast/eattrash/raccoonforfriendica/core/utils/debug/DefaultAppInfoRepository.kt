package com.livefast.eattrash.raccoonforfriendica.core.utils.debug

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow

class DefaultAppInfoRepository(
    private val context: Context,
) : AppInfoRepository {
    override val appInfo = MutableStateFlow(geInfo())

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
