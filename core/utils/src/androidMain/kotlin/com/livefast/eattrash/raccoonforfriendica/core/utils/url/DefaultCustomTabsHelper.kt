package com.livefast.eattrash.raccoonforfriendica.core.utils.url

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultCustomTabsHelper(private val context: Context) : CustomTabsHelper {
    private val packageName: String?
        get() = CustomTabsClient.getPackageName(context, emptyList())

    override val isSupported: Boolean by lazy {
        !packageName.isNullOrEmpty()
    }

    override fun handle(url: String) {
        val uri = url.toUri()
        CustomTabsIntent
            .Builder()
            .apply {
                setShareState(CustomTabsIntent.SHARE_STATE_ON)
                setShowTitle(true)
            }.build()
            .run {
                intent.apply {
                    `package` = packageName
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                launchUrl(context, uri)
            }
    }
}
