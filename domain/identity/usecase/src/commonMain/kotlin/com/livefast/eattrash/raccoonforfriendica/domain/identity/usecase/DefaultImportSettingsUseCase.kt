package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@ContributesBinding(AppScope::class)
@Inject
class DefaultImportSettingsUseCase(private val settingsRepository: SettingsRepository) : ImportSettingsUseCase {
    override suspend fun invoke(content: String) {
        val current = settingsRepository.current.value ?: return
        withContext(Dispatchers.IO) {
            val data: SerializableSettings = jsonSerializationStrategy.decodeFromString(content)
            val settings =
                data.toModel().copy(
                    id = current.id,
                    accountId = current.accountId,
                )
            settingsRepository.update(settings)
            settingsRepository.changeCurrent(settings)
        }
    }
}
