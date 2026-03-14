package com.livefast.eattrash.raccoonforfriendica.auth

import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CompletableDeferred
import java.net.ServerSocket

class EmbeddedRedirectServer {
    private var embeddedServer: EmbeddedServer<*, *>? = null
    private val codeDeferred = CompletableDeferred<String>()

    fun start(): Int {
        val port = findFreePort()
        embeddedServer = embeddedServer(CIO, port = port) {
            routing {
                get("/") {
                    val code = call.parameters["code"]
                    if (code != null) {
                        codeDeferred.complete(code)
                        call.respondText("Authorization successful! You can close this tab.")
                    } else {
                        call.respondText("Authorization failed: No code received.")
                    }
                }
            }
        }.start(wait = false)
        return port
    }

    suspend fun waitForCode(): String = codeDeferred.await()

    fun stop() {
        embeddedServer?.stop(1000, 2000)
    }

    private fun findFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }
}
