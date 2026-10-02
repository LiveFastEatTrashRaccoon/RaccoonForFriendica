package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.InboxManager
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@ContributesIntoMap(
    scope = AppScope::class,
    binding = binding<@ViewModelKey ViewModel>(),
)
@Inject
class PermanentDrawerViewModel(
    private val identityRepository: IdentityRepository,
    private val inboxManager: InboxManager,
    private val supportedFeatureRepository: SupportedFeatureRepository,
) : ViewModel(),
    MviDelegate<PermanentDrawerMvi.Intent, PermanentDrawerMvi.State, PermanentDrawerMvi.Effect>
    by DefaultMviDelegate(initialState = PermanentDrawerMvi.State()),
    PermanentDrawerMvi {
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

    override fun reduce(intent: PermanentDrawerMvi.Intent) {
        when (intent) {
            PermanentDrawerMvi.Intent.ToggleExpanded -> viewModelScope.launch {
                updateState {
                    it.copy(isExpanded = !it.isExpanded)
                }
            }
        }
    }
}
