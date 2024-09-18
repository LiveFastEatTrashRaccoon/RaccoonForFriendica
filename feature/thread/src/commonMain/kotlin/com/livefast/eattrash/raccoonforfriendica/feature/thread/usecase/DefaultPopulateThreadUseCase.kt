package com.livefast.eattrash.raccoonforfriendica.feature.thread.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import com.livefast.eattrash.raccoonforfriendica.feature.thread.data.ConversationNode
import kotlinx.coroutines.coroutineScope

internal class DefaultPopulateThreadUseCase(
    private val timelineEntryRepository: TimelineEntryRepository,
) : PopulateThreadUseCase {
    override suspend fun invoke(entryId: String): List<TimelineEntryModel> {
        val entry = timelineEntryRepository.getById(entryId) ?: return emptyList()
        val root = ConversationNode(entry)
        populateTree(root)
        return mutableListOf<TimelineEntryModel>()
            .apply {
                linearize(root)
            }.deduplicate()
            .populateLoadMore()
            .toList()
    }

    private suspend fun populateTree(
        node: ConversationNode,
        depth: Int = 0,
    ): Unit =
        coroutineScope {
            val entry = node.entry
            if (entry.replyCount == 0) {
                // base case 0: entry has no children
                return@coroutineScope
            }
            if (depth >= MAX_DEPTH) {
                // base case 1: max depth level reached
                return@coroutineScope
            }

            // return all direct descendants
            val descendants = timelineEntryRepository.getContext(entry.id)?.descendants.orEmpty()
            val childNodes =
                descendants
                    .mapNotNull { child ->
                        // needed because otherwise replies of different depths are included
                        val referenceChild = child.original
                        if (referenceChild.inReplyTo?.id == entry.id || referenceChild.inReplyTo?.id == entry.reblog?.id) {
                            ConversationNode(entry = referenceChild.copy(depth = depth + 1))
                        } else {
                            null
                        }
                    }
            for (child in childNodes) {
                populateTree(node = child, depth = depth + 1)
            }
            node.children.addAll(childNodes)
        }

    private fun MutableList<TimelineEntryModel>.linearize(node: ConversationNode) {
        add(node.entry)
        for (child in node.children) {
            linearize(child)
        }
    }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> {
        val res = mutableListOf<TimelineEntryModel>()
        for (e in this) {
            if (res.none { it.id == e.id }) {
                res += e
            }
        }
        return res
    }

    private fun List<TimelineEntryModel>.populateLoadMore() =
        mapIndexed { idx, entry ->
            val hasMoreReplies = entry.replyCount > 0
            val isNextCommentNotChild =
                (idx < lastIndex && this[idx + 1].depth <= entry.depth) || idx == lastIndex
            entry.copy(loadMoreButtonVisible = hasMoreReplies && isNextCommentNotChild)
        }

    companion object {
        private const val MAX_DEPTH = 5
    }
}
