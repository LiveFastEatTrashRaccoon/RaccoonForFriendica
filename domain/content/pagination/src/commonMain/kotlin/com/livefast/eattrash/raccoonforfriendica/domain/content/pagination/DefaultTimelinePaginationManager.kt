package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.isNsfw
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository

internal class DefaultTimelinePaginationManager(
    private val timelineRepository: TimelineRepository,
    private val timelineEntryRepository: TimelineEntryRepository,
) : TimelinePaginationManager {
    private var specification: TimelinePaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    override val history = mutableListOf<TimelineEntryModel>()

    override suspend fun reset(specification: TimelinePaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
        canFetchMore = true
    }

    override suspend fun loadNextPage(): List<TimelineEntryModel> {
        val specification = this.specification ?: return emptyList()

        val results =
            when (specification) {
                is TimelinePaginationSpecification.Feed -> {
                    when (specification.timelineType) {
                        TimelineType.All ->
                            timelineRepository.getPublic(
                                pageCursor = pageCursor,
                            )

                        TimelineType.Subscriptions ->
                            timelineRepository.getHome(
                                pageCursor = pageCursor,
                            )

                        TimelineType.Local ->
                            timelineRepository.getLocal(
                                pageCursor = pageCursor,
                            )
                    }.apply {
                        lastOrNull()?.also {
                            pageCursor = it.id
                        }
                        canFetchMore = isNotEmpty()
                    }.filterNsfw(specification.includeNsfw)
                }

                is TimelinePaginationSpecification.Hashtag -> {
                    timelineRepository
                        .getHashtag(specification.hashtag)
                        .apply {
                            lastOrNull()?.also {
                                pageCursor = it.id
                            }
                            canFetchMore = isNotEmpty()
                        }.filterNsfw(specification.includeNsfw)
                }

                is TimelinePaginationSpecification.User ->
                    timelineEntryRepository
                        .getByUser(
                            userId = specification.userId,
                            pageCursor = pageCursor,
                            excludeReplies = specification.excludeReplies,
                            excludeReblogs = specification.excludeReblogs,
                            onlyMedia = specification.onlyMedia,
                            pinned = specification.pinned,
                        ).apply {
                            lastOrNull()?.also {
                                pageCursor = it.id
                            }
                            canFetchMore = isNotEmpty()
                        }.filterNsfw(specification.includeNsfw)
            }.deduplicate()
        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    override fun extractState(): TimelinePaginationManagerState =
        DefaultTimelinePaginationManagerState(
            specification = specification,
            history = history,
            pageCursor = pageCursor,
            canFetchMore = canFetchMore,
        )

    override fun restoreState(state: TimelinePaginationManagerState) {
        (state as? DefaultTimelinePaginationManagerState)?.also {
            specification = it.specification
            pageCursor = it.pageCursor
            canFetchMore = it.canFetchMore
            history.clear()
            history.addAll(it.history)
        }
    }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }

    private fun List<TimelineEntryModel>.filterNsfw(included: Boolean): List<TimelineEntryModel> = filter { included || !it.isNsfw }
}
