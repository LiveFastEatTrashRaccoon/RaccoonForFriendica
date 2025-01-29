package com.livefast.eattrash.raccoonforfriendica.feature.shortcuts.list

import cafe.adriel.voyager.core.model.screenModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModel
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
) : DefaultMviModel<ShortcutListMviModel.Intent, ShortcutListMviModel.State, ShortcutListMviModel.Effect>(
        initialState = ShortcutListMviModel.State(),
    ),
    ShortcutListMviModel {
    init {
        screenModelScope.launch {
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
                screenModelScope.launch {
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
        screenModelScope.launch {
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
