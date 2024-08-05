package com.livefast.eattrash.raccoonforfriendica.core.utils.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun provideHttpClientEngine(): HttpClientEngine = Android.create()
