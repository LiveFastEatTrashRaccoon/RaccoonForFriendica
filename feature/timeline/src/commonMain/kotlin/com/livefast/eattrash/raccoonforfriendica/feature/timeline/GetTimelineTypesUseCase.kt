package com.livefast.eattrash.raccoonforfriendica.feature.timeline

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import kotlinx.coroutines.flow.Flow

/**
 * Provides a stream of the timeline types available to the current user.
 *
 * This includes built-in timeline types (e.g., All, Local, Subscriptions) as well as
 * user-defined circles. The stream will automatically emit a new list
 * when the underlying configuration (such as the active account) changes, or when
 * [refresh] is called manually.
 */
interface GetTimelineTypesUseCase {

    /**
     * Returns a [Flow] that emits the list of available [TimelineType]s.
     */
    operator fun invoke(): Flow<List<TimelineType>>

    /**
     * Forces a re-evaluation of the available timeline types.
     *
     * Calling this will cause the [Flow] returned by [invoke] to emit a new list.
     */
    suspend fun refresh()
}
