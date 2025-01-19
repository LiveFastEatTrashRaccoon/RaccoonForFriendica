package com.livefast.eattrash.raccoonforfriendica.core.api.service

interface InnerTranslationService {
    suspend fun translate(
        sourceText: String,
        sourceLang: String,
        targetLang: String,
    ): String?
}
