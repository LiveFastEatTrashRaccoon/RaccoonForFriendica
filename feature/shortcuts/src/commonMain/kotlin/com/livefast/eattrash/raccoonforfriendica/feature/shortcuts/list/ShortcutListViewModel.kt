package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.InstanceShortcutRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShortcutListViewModel(
    private val shortcutRepository: InstanceShortcutRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel(),
    MviModelDelegate<ShortcutListMviModel.Intent, ShortcutListMviModel.State, ShortcutListMviModel.Effect>
    by DefaultMviModelDelegate(initialState = ShortcutListMviModel.State()),
    ShortcutListMviModel {
    init {
        viewModelScope.launch {
            settingsRepository.current
                .onEach { settings ->
                    updateState {
                        it.copy(
                            hideNavigationBarWhileScrolling =
                            settings?.hideNavigationBarWhileScrolling ?: true,
                        )
                    }
                }.launchIn(this)

            if (uiState.value.initial) {
                refresh(initial = true)
            }
        }
    }

    override fun reduce(intent: ShortcutListMviModel.Intent) {
        when (intent) {
            ShortcutListMviModel.Intent.Refresh ->
                viewModelScope.launch {
                    refresh()
                }

            is ShortcutListMviModel.Intent.Delete -> delete(intent.node)
        }
    }

    private suspend fun refresh(initial: Boolean = false) {
        updateState {
            it.copy(
                initial = initial,
                refreshing = !initial,
            )
        }
        val accountId = accountRepository.getActive()?.id ?: 0
        val shortcuts = shortcutRepository.getAll(accountId)
        updateState {
            it.copy(
                initial = false,
                refreshing = false,
                items = shortcuts,
            )
        }
    }

    private fun delete(node: String) {
        viewModelScope.launch {
            updateState {
                it.copy(operationInProgress = true)
            }
            val accountId = accountRepository.getActive()?.id ?: 0
            val shortcuts = shortcutRepository.delete(accountId = accountId, node = node)
            updateState {
                it.copy(
                    operationInProgress = false,
                    items = shortcuts,
                )
            }
        }
    }
}
