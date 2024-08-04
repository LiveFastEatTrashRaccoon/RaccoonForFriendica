package com.github.akesiseli.raccoonforfriendica.core.utils.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun provideHttpClientEngine(): HttpClientEngine = Darwin.create()
