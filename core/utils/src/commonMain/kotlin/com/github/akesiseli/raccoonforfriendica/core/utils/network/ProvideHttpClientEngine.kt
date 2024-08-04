package com.github.akesiseli.raccoonforfriendica.core.utils.network

import io.ktor.client.engine.HttpClientEngine

expect fun provideHttpClientEngine(): HttpClientEngine
