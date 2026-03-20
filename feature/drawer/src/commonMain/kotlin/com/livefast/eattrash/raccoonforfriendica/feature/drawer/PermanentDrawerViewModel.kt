package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PermanentDrawerViewModel(
    private val identityRepository: IdentityRepository,
    private val inboxManager: InboxManager,
    private val supportedFeatureRepository: SupportedFeatureRepository,
) : ViewModel(),
    MviModelDelegate<PermanentDrawerMviModel.Intent, PermanentDrawerMviModel.State, PermanentDrawerMviModel.Effect>
    by DefaultMviModelDelegate(initialState = PermanentDrawerMviModel.State()),
    PermanentDrawerMviModel {
    init {
        viewModelScope.launch {
            identityRepository.currentUser.onEach { currentUser ->
                updateState {
                    it.copy(isLogged = currentUser != null)
                }
            }.launchIn(this)
            inboxManager.unreadCount
                .onEach { inboxUnread ->
                    updateState {
                        it.copy(unreadItems = inboxUnread)
                    }
                }.launchIn(this)
            supportedFeatureRepository.features
                .onEach { features ->
                    updateState {
                        it.copy(
                            hasDirectMessages = features.supportsDirectMessages,
                            hasGallery = features.supportsPhotoGallery,
                            hasCalendar = features.supportsCalendar,
                            hasAnnouncements = features.supportsAnnouncements,
                        )
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: PermanentDrawerMviModel.Intent) {
        when (intent) {
            PermanentDrawerMviModel.Intent.ToggleExpanded -> viewModelScope.launch {
                updateState {
                    it.copy(isExpanded = !it.isExpanded)
                }
            }
        }
    }
}
