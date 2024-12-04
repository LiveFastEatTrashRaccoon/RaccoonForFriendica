package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
internal expect class DefaultAppInfoRepository : AppInfoRepository {
    override val appInfo: StateFlow<AppInfo?>
}
