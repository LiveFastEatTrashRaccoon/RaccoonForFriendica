package com.livefast.eattrash.raccoonforfriendica.feature.settings.translationconfig

import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModel
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig

interface TranslationConfigMviModel :
    MviModel<TranslationConfigMviModel.Intent, TranslationConfigMviModel.State, TranslationConfigMviModel.Effect> {
    sealed interface Intent {
        data class SwitchDefault(val config: TranslationProviderConfig) : Intent
        data class AddConfig(val url: String, val apiKey: String) : Intent
        data class DeleteConfig(val config: TranslationProviderConfig) : Intent
    }

    data class State(val configs: List<TranslationProviderConfig> = emptyList())

    sealed interface Effect {
        data object Success : Effect
        data object Failure : Effect
    }
}
