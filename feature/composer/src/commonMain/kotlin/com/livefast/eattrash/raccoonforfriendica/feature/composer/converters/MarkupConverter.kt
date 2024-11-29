package com.livefast.eattrash.raccoonforfriendica.feature.composer.converters

interface MarkupConverter {
    fun toHtml(value: String): String

    fun fromHtml(value: String): String
}
