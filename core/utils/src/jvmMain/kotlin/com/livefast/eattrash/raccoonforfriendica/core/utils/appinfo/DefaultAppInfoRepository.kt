package com.livefast.eattrash.raccoonforfriendica.core.utils.appinfo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.management.ManagementFactory
import java.util.Properties

internal class DefaultAppInfoRepository : AppInfoRepository {
    private val _appInfo = MutableStateFlow(getInfo())

    override val appInfo: StateFlow<AppInfo> = _appInfo.asStateFlow()

    private fun getInfo(): AppInfo {
        return AppInfo(
            versionCode = getAppVersion(),
            isDebug = isDebug(),
        )
    }

    private fun getAppVersion(): String {
        return try {
            val p = Properties()
            val inputStream = this.javaClass.getResourceAsStream(PROPERTIES_PATH)
            if (inputStream != null) {
                p.load(inputStream)
                p.getProperty(PROPERTY_KEY, DEFAULT_VALUE)
            } else {
                DEFAULT_VALUE
            }
        } catch (_: Exception) {
            DEFAULT_VALUE
        }
    }

    private fun isDebug(): Boolean {
        val inputArguments = ManagementFactory.getRuntimeMXBean().inputArguments
        return inputArguments.any { it.contains("-agentlib:jdwp") || it.contains("-Xdebug") }
    }

    companion object {
        private const val PROPERTIES_PATH = "/version.properties"
        private const val PROPERTY_KEY = "version"
        private const val DEFAULT_VALUE = "N/A"
    }
}
