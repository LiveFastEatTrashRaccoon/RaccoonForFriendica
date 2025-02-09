package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository
import kotlinx.coroutines.coroutineScope

private data class ConversationNode(
    val entry: TimelineEntryModel,
    val children: MutableList<ConversationNode> = mutableListOf(),
)

internal class DefaultPopulateThreadUseCase(
    private val timelineEntryRepository: TimelineEntryRepository,
    private val emojiHelper: EmojiHelper,
) : PopulateThreadUseCase {
    override suspend fun invoke(
        entry: TimelineEntryModel,
        maxDepth: Int,
    ): List<TimelineEntryModel> {
        val root = ConversationNode(entry)
        populateTree(
            node = root,
            maxDepth = maxDepth,
            depthOffset = entry.depth,
            depth = 0,
        )
        val res =
            mutableListOf<TimelineEntryModel>()
                .apply {
                    linearize(root)
                }.deduplicate()
                .populateLoadMore()
        return res.toList()
    }

    private suspend fun populateTree(
        node: ConversationNode,
        depth: Int,
        depthOffset: Int = 0,
        maxDepth: Int,
    ): Unit =
        coroutineScope {
            val entry = node.entry
            if (entry.replyCount == 0) {
                // base case 0: entry has no children
                return@coroutineScope
            }
            if (depth >= maxDepth) {
                // base case 1: max depth level reached
                return@coroutineScope
            }

            // return all direct descendants
            val descendants =
                timelineEntryRepository
                    .getContext(entry.id)
                    ?.descendants
                    .orEmpty()
                    .map {
                        with(emojiHelper) { it.withEmojisIfMissing() }
                    }
            val childNodes =
                descendants
                    .mapNotNull { child ->
                        // needed because otherwise replies of different depths are included
                        val referenceChild = child.original
                        if (referenceChild.inReplyTo?.id == entry.id || referenceChild.inReplyTo?.id == entry.reblog?.id) {
                            ConversationNode(
                                entry =
                                    referenceChild.copy(
                                        depth = depthOffset + depth + 1,
                                    ),
                            )
                        } else {
                            null
                        }
                    }
            for (child in childNodes) {
                populateTree(
                    node = child,
                    depth = depth + 1,
                    depthOffset = depthOffset,
                    maxDepth = maxDepth,
                )
            }
            node.children.addAll(childNodes)
        }

    private fun MutableList<TimelineEntryModel>.linearize(node: ConversationNode) {
        add(node.entry)
        for (child in node.children) {
            linearize(child)
        }
    }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> = distinctBy { it.safeKey }

    private fun List<TimelineEntryModel>.populateLoadMore() =
        mapIndexed { idx, entry ->
            val hasMoreReplies = entry.replyCount > 0
            val isNextCommentNotChild =
                (idx < lastIndex && this[idx + 1].depth <= entry.depth) || idx == lastIndex
            entry.copy(loadMoreButtonVisible = hasMoreReplies && isNextCommentNotChild)
        }
}
