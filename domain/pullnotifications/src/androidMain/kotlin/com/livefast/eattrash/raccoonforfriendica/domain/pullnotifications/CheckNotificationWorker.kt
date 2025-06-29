package com.livefast.eattrash.raccoonforfriendica.domain.pullnotifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.l10n.di.getStrings
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.instance
import java.util.Collections.max

internal class CheckNotificationWorker(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private val inboxManager by RootDI.di.instance<InboxManager>()
    private val settingsRepository by RootDI.di.instance<SettingsRepository>()
    private val strings = getStrings(settingsRepository.current.value?.lang ?: "en")
    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun getForegroundInfo(): ForegroundInfo = ForegroundInfo(
        getNextNotificationId(),
        Notification
            .Builder(context, NotificationConstants.CHANNEL_ID)
            .setContentTitle("RaccoonForFriendica")
            .setSmallIcon(R.drawable.ic_monochrome)
            .setContentIntent(getPendingIntent())
            .build(),
    )

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        inboxManager.refreshUnreadCount()
        val unread = inboxManager.unreadCount.value
        if (unread > 0) {
            sendNotification(unread)
        }
        Result.success()
    }

    private suspend fun sendNotification(count: Int) {
        try {
            val notification =
                Notification
                    .Builder(context, NotificationConstants.CHANNEL_ID)
                    .setContentTitle(strings.unreadNotificationTitle())
                    .setContentText(strings.unreadNotificationBody(count))
                    .setSmallIcon(R.drawable.ic_monochrome)
                    .setContentIntent(getPendingIntent())
                    .setNumber(count)
                    .build()
            val notificationId = getNextNotificationId()
            notificationManager.notify(
                NotificationConstants.NOTIFICATION_TAG,
                notificationId,
                notification,
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent =
            Intent(
                context,
                Class.forName(ACTIVITY_NAME),
            ).apply {
                putExtra(InboxManager.OPEN_INBOX_AT_STARTUP, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun getNextNotificationId(): Int {
        val activeNotifications =
            notificationManager.activeNotifications.filter { it.tag == context.packageName }
        return if (activeNotifications.isEmpty()) {
            0
        } else {
            max(activeNotifications.map { it.id }) + 1
        }
    }

    companion object {
        private const val ACTIVITY_NAME = "com.livefast.eattrash.raccoonforfriendica.MainActivity"
    }
}
