package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString

internal class DefaultExportSettingsUseCase(private val settingsRepository: SettingsRepository) :
    ExportSettingsUseCase {
    override suspend fun invoke(): String {
        val settings = settingsRepository.current.value ?: return ""
        return withContext(Dispatchers.IO) {
            val data = settings.toData()
            jsonSerializationStrategy.encodeToString(data)
        }
    }
}
