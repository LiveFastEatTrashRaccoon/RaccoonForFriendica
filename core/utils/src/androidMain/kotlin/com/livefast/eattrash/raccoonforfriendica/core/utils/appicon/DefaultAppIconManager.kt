package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

class DefaultAppIconManager(
    private val context: Context,
) : AppIconManager {
    private val allComponentNames =
        listOf(
            "com.livefast.eattrash.raccoonforfriendica.MainActivity",
            "com.livefast.eattrash.raccoonforfriendica.MainActivityAlias",
        )

    override val supportsMultipleIcons = allComponentNames.isNotEmpty()

    override fun changeIcon(variant: AppIconVariant) {
        val indexToEnable = variant.toInt()
        with(context.packageManager) {
            allComponentNames.forEachIndexed { i, name ->
                val enabledState =
                    if (i == indexToEnable) {
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    } else {
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    }
                setComponentEnabledSetting(
                    ComponentName(context, name),
                    enabledState,
                    PackageManager.DONT_KILL_APP,
                )
            }
        }
    }
}
