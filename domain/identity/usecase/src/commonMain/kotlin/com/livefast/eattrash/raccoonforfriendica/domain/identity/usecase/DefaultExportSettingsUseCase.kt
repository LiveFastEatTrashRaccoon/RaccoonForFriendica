package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultExportSettingsUseCase(private val settingsRepository: SettingsRepository) : ExportSettingsUseCase {
    override suspend fun invoke(): String {
        val settings = settingsRepository.current.value ?: return ""
        return withContext(Dispatchers.IO) {
            val data = settings.toData()
            jsonSerializationStrategy.encodeToString(data)
        }
    }
}
