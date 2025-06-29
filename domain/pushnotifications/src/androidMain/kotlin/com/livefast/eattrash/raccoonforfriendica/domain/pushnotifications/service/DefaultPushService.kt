package com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.service

import com.livefast.eattrash.raccoonforfriendica.core.di.RootDI
import com.livefast.eattrash.raccoonforfriendica.core.utils.debug.logDebug
import com.livefast.eattrash.raccoonforfriendica.domain.pushnotifications.common.UnifiedPushInteractor
import org.kodein.di.instance
import org.unifiedpush.android.connector.FailedReason
import org.unifiedpush.android.connector.PushService
import org.unifiedpush.android.connector.data.PushEndpoint
import org.unifiedpush.android.connector.data.PushMessage

class DefaultPushService : PushService() {
    private val interactor by RootDI.di.instance<UnifiedPushInteractor>()

    override fun onCreate() {
        super.onCreate()
        logDebug("DefaultPushService - DefaultPushService onCreate")
    }

    override fun onMessage(message: PushMessage, instance: String) {
        interactor.onMessage(context = this, message = message, instance = instance)
    }

    override fun onNewEndpoint(endpoint: PushEndpoint, instance: String) {
        interactor.onNewEndpoint(context = this, endpoint = endpoint, instance = instance)
    }

    override fun onRegistrationFailed(reason: FailedReason, instance: String) {
        interactor.onRegistrationFailed(context = this, reason = reason, instance = instance)
    }

    override fun onUnregistered(instance: String) {
        interactor.onUnregistered(context = this, instance = instance)
    }
}
