package com.livefast.eattrash.raccoonforfriendica.core.architecture

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Delegate interface which exposes some utility functions to all [Mvi] implementors.
 *
 * @param Intent class of view intents
 * @param State class of UI state
 * @param Effect class of effects
 */
interface MviDelegate<Intent, State, Effect> : Mvi<Intent, State, Effect> {
    /**
     * Emit an effect (one-shot event).
     *
     * @param value Value
     */
    suspend fun emitEffect(value: Effect)

    /**
     * Update the UI state.
     *
     * @param block Block
     */
    suspend fun updateState(block: (State) -> State)
}

/**
 * Default implementation of [MviDelegate].
 *
 * @param initialState initial UI state
 */
class DefaultMviDelegate<Intent, State, Effect>(initialState: State) : MviDelegate<Intent, State, Effect> {
    override val uiState = MutableStateFlow(initialState)
    override val effects = MutableSharedFlow<Effect>()
    private val mutex = Mutex()

    override suspend fun emitEffect(value: Effect) {
        effects.emit(value)
    }

    override suspend fun updateState(block: (State) -> State) {
        mutex.withLock {
            uiState.update { block(uiState.value) }
        }
    }

    override fun reduce(intent: Intent) = Unit
}
