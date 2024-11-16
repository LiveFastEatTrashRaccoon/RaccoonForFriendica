package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.livefast.eattrash.raccoonforfriendica.core.preferences.TemporaryKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DefaultAppIconManager(
    private val context: Context,
    private val keyStore: TemporaryKeyStore,
) : AppIconManager {
    override val current = MutableStateFlow<AppIconVariant>(AppIconVariant.Default)
    private val allComponentNames =
        listOf(
            "com.livefast.eattrash.raccoonforfriendica.MainActivity",
            "com.livefast.eattrash.raccoonforfriendica.MainActivityAlias",
        )

    override val supportsMultipleIcons = allComponentNames.isNotEmpty()

    init {
        val lastUsedVariant = keyStore[KEY_APP_ICON_VARIANT, 0].toAppIconVariant()
        current.update { lastUsedVariant }
    }

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
        keyStore.save(KEY_APP_ICON_VARIANT, variant.toInt())
        current.update { variant }
    }

    companion object {
        private const val KEY_APP_ICON_VARIANT = "AppIconManager.APP_ICON_VARIANT"
    }
}
