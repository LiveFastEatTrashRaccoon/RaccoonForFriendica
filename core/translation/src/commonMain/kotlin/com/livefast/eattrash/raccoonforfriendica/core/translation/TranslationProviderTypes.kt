package com.livefast.eattrash.raccoonforfriendica.core.translation

sealed interface TranslationProviderTypes {
    val name: String

    object LibreTranslate : TranslationProviderTypes {
        override val name = "LibreTranslate"
    }
}
