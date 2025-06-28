package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.receiver

import android.content.Context
import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.UnifiedPushInteractor
import org.kodein.di.instance
import org.unifiedpush.android.connector.FailedReason
import org.unifiedpush.android.connector.MessagingReceiver
import org.unifiedpush.android.connector.data.PushEndpoint
import org.unifiedpush.android.connector.data.PushMessage

class UnifiedPushBroadcastReceiver : MessagingReceiver() {
    private val interactor by RootDI.di.instance<UnifiedPushInteractor>()

    override fun onMessage(context: Context, message: PushMessage, instance: String) {
        interactor.onMessage(context = context, message = message, instance = instance)
    }

    override fun onNewEndpoint(context: Context, endpoint: PushEndpoint, instance: String) {
        interactor.onNewEndpoint(context = context, endpoint = endpoint, instance = instance)
    }

    override fun onRegistrationFailed(context: Context, reason: FailedReason, instance: String) {
        interactor.onRegistrationFailed(context = context, reason = reason, instance = instance)
    }

    override fun onUnregistered(context: Context, instance: String) {
        interactor.onUnregistered(context = context, instance = instance)
    }
}
