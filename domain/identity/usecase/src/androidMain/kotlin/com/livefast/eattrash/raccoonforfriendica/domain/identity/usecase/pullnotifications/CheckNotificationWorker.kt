package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase.pullnotifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.livefast.eattrash.raccoonforfriendica.core.l10n.L10nManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.util.Collections.max
import com.livefast.eattrash.raccoonforfriendica.core.resources.R as resourcesR

internal class CheckNotificationWorker(
    private val context: Context,
    parameters: WorkerParameters,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CoroutineWorker(context, parameters) {
    private val inboxManager by inject<InboxManager>(InboxManager::class.java)
    private val l10nManager by inject<L10nManager>(L10nManager::class.java)

    override suspend fun getForegroundInfo(): ForegroundInfo =
        ForegroundInfo(
            getNextNotificationId(),
            Notification
                .Builder(context, NotificationConstants.CHANNEL_ID)
                .setContentTitle("RaccoonForFriendica")
                .setSmallIcon(resourcesR.drawable.ic_monochrome)
                .setContentIntent(getPendingIntent())
                .build(),
        )

    override suspend fun doWork(): Result =
        withContext(dispatcher) {
            inboxManager.refreshUnreadCount()
            val unread = inboxManager.unreadCount.value
            if (unread > 0) {
                sendNotification(unread)
            }
            Result.success()
        }

    private fun sendNotification(count: Int) {
        val messages = l10nManager.messages()
        val notification =
            Notification
                .Builder(context, NotificationConstants.CHANNEL_ID)
                .setContentTitle(messages.unreadNotificationTitle)
                .setContentText(messages.unreadNotificationBody(count))
                .setSmallIcon(resourcesR.drawable.ic_monochrome)
                .setContentIntent(getPendingIntent())
                .setNumber(count)
                .build()
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = getNextNotificationId()
        notificationManager.notify(
            NotificationConstants.NOTIFICATION_TAG,
            notificationId,
            notification,
        )
    }

    private fun getPendingIntent(): PendingIntent {
        val intent =
            Intent(
                context,
                Class.forName(ACTIVITY_NAME),
            ).apply {
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
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
