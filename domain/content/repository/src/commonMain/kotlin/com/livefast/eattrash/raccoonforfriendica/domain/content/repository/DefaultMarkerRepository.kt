package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.MarkerType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
internal class DefaultMarkerRepository(
    @Named("default") private val provider: ServiceProvider,
) : MarkerRepository {
    private val cachedValues = mutableMapOf<MarkerType, MarkerModel>()

    override suspend fun get(
        type: MarkerType,
        refresh: Boolean,
    ): MarkerModel? =
        withContext(Dispatchers.IO) {
            if (cachedValues.contains(type) && !refresh) {
                cachedValues[type]
            } else {
                runCatching {
                    provider.markers
                        .get(timelines = listOf(type.toDto()))
                        .toModel()
                        .firstOrNull { it.type == type }
                        ?.also { cachedValues[type] = it }
                }.getOrNull()
            }
        }

    override suspend fun update(
        type: MarkerType,
        id: String,
    ): MarkerModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                val fieldName = "${type.toDto()}[last_read_id]"
                val data =
                    FormDataContent(
                        Parameters.build {
                            append(name = fieldName, value = id)
                        },
                    )
                provider.markers
                    .update(data)
                    .toModel()
                    .firstOrNull { it.type == type }
                    ?.also { cachedValues[type] = it }
            }.getOrNull()
        }
}
