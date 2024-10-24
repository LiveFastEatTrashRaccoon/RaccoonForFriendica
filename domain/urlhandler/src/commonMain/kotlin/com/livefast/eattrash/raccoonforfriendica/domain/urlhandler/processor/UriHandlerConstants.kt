package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

internal object UriHandlerConstants {
    const val DETAIL_FRAGMENT: String = "[a-zA-Z0-9_]{3,}"
    const val INSTANCE_FRAGMENT: String =
        "([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.)+[a-zA-Z]{2,}"
}
