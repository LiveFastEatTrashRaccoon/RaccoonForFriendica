package com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils

import de.jensklingenberg.ktorfit.Response

internal fun <T> Response<T>.extractNextIdFromResponseLinkHeader(): String? = headers["link"]
    .orEmpty()
    .let {
        val match = Regex("max_id=(?<maxId>\\d+)>").find(it)
        match?.groups?.get("maxId")?.value
    }
