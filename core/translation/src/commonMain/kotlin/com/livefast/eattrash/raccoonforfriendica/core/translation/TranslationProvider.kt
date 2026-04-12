package com.livefast.eattrash.raccoonforfriendica.core.translation

interface TranslationProvider {
    suspend fun translate(sourceText: String, sourceLang: String, targetLang: String): String
}
