package com.livefast.eattrash.raccoonforfriendica.core.utils.validation

import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException

actual fun String.isValidUrl(): Boolean {
    return try {
        val url = URI(this)
        url.toURL()
        url.scheme.isNotEmpty()
    } catch (e: MalformedURLException) {
        false
    } catch (e: URISyntaxException) {
        false
    }
}
