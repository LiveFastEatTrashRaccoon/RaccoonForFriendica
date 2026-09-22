package com.livefast.eattrash.raccoonforfriendica.feature.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livefast.eattrash.raccoonforfriendica.core.architecture.DefaultMviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.core.architecture.MviModelDelegate
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.IdentityRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ImageAutoloadObserver
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
class DrawerViewModel(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val identityRepository: IdentityRepository,
    private val supportedFeatureRepository: SupportedFeatureRepository,
    private val emojiHelper: EmojiHelper,
    private val imageAutoloadObserver: ImageAutoloadObserver,
    accountRepository: AccountRepository,
) : ViewModel(),
    MviModelDelegate<DrawerMviModel.Intent, DrawerMviModel.State, DrawerMviModel.Effect>
    by DefaultMviModelDelegate(initialState = DrawerMviModel.State()),
    DrawerMviModel {
    init {
        viewModelScope.launch {
            imageAutoloadObserver.enabled
                .onEach { autoloadImages ->
                    updateState {
                        it.copy(
                            autoloadImages = autoloadImages,
                        )
                    }
                }.launchIn(this)
            identityRepository.currentUser
                .onEach { currentUser ->
                    val user =
                        with(emojiHelper) {
                            currentUser?.withEmojisIfMissing()
                        }
                    updateState {
                        it.copy(user = user)
                    }
                }.launchIn(this)
            apiConfigurationRepository.node
                .onEach { node ->
                    updateState { it.copy(node = node) }
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
            accountRepository
                .getAllAsFlow()
                .onEach { accounts ->
                    val nonAnonymousAccounts = accounts.filter { it.remoteId != null }
                    updateState {
                        it.copy(canSwitchAccount = nonAnonymousAccounts.isNotEmpty())
                    }
                }.launchIn(this)
        }
    }

    override fun reduce(intent: DrawerMviModel.Intent) = Unit
}
