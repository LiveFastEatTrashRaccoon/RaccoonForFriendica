package com.livefast.eattrash.raccoonforfriendica.core.utils.appicon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.livefast.eattrash.raccoonforfriendica.core.preferences.store.TemporaryKeyStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultAppIconManager(private val context: Context, private val keyStore: TemporaryKeyStore) :
    AppIconManager {
    override val current: StateFlow<AppIconVariant> field = MutableStateFlow<AppIconVariant>(AppIconVariant.Default)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val allComponentNames =
        listOf(
            "com.livefast.eattrash.raccoonforfriendica.MainActivity",
            "com.livefast.eattrash.raccoonforfriendica.MainActivityAlias",
        )

    override val supportsMultipleIcons = allComponentNames.isNotEmpty()

    init {
        scope.launch {
            val lastUsedVariant = keyStore.get(KEY_APP_ICON_VARIANT, 0).toAppIconVariant()
            current.update { lastUsedVariant }
        }
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
        scope.launch {
            keyStore.save(KEY_APP_ICON_VARIANT, variant.toInt())
        }
        current.update { variant }
    }

    companion object {
        private const val KEY_APP_ICON_VARIANT = "AppIconManager.APP_ICON_VARIANT"
    }
}
