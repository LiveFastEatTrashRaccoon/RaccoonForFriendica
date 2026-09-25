package com.livefast.eattrash.raccoonforfriendica.feature.settings.translationconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderConfig
import com.livefast.eattrash.raccoonforfriendica.core.translation.TranslationProviderTypes
import com.livefast.eattrash.raccoonforfriendica.core.translation.store.TranslationProviderConfigStore
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TranslationConfigViewModel(private val translationProviderConfigStore: TranslationProviderConfigStore) :
    ViewModel(),
    MviModelDelegate<
        TranslationConfigMviModel.Intent,
        TranslationConfigMviModel.State,
        TranslationConfigMviModel.Effect,
        > by DefaultMviModelDelegate(initialState = TranslationConfigMviModel.State()),
    TranslationConfigMviModel {

    init {
        viewModelScope.launch {
            translationProviderConfigStore.observe().onEach { configs ->
                updateState { it.copy(configs = configs) }
            }.launchIn(this)
        }
    }

    override fun reduce(intent: TranslationConfigMviModel.Intent) {
        when (intent) {
            is TranslationConfigMviModel.Intent.SwitchDefault -> viewModelScope.launch {
                translationProviderConfigStore.setDefaultId(intent.config.id)
            }

            is TranslationConfigMviModel.Intent.AddConfig -> viewModelScope.launch {
                val config = TranslationProviderConfig(
                    name = TranslationProviderTypes.LibreTranslate.name,
                    url = intent.url,
                    apiKey = intent.apiKey,
                )
                translationProviderConfigStore.create(config)
            }

            is TranslationConfigMviModel.Intent.DeleteConfig -> viewModelScope.launch {
                translationProviderConfigStore.delete(intent.config.id)
            }
        }
    }
}
