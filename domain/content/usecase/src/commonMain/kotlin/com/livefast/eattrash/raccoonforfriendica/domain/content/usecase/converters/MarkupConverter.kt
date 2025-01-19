package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters

interface MarkupConverter {
    fun toHtml(value: String): String

    fun fromHtml(value: String): String
}
