package com.livefast.eattrash.raccoonforfriendica.domain.content.pagination

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineType
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineRepository

internal class DefaultTimelinePaginationManager(
    private val timelineRepository: TimelineRepository,
) : TimelinePaginationManager {
    private var specification: TimelinePaginationSpecification? = null
    private var pageCursor: String? = null
    override var canFetchMore: Boolean = true
    private val history = mutableListOf<TimelineEntryModel>()

    override suspend fun reset(specification: TimelinePaginationSpecification) {
        this.specification = specification
        pageCursor = null
        history.clear()
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
                    }
                }

                is TimelinePaginationSpecification.Hashtag -> {
                    timelineRepository.getHashtag(specification.hashtag)
                }

                is TimelinePaginationSpecification.Account ->
                    timelineRepository.getByAccount(
                        accountId = specification.accountId,
                        pageCursor = pageCursor,
                        excludeReplies = specification.excludeReplies,
                        excludeReblogs = specification.excludeReblogs,
                        onlyMedia = specification.onlyMedia,
                        pinned = specification.pinned,
                    )
            }.deduplicate()

        if (results.isNotEmpty()) {
            pageCursor = results.last().id
            canFetchMore = true
        } else {
            canFetchMore = false
        }

        history.addAll(results)

        // return a copy
        return history.map { it }
    }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> =
        filter { e1 ->
            history.none { e2 -> e1.id == e2.id }
        }
}
