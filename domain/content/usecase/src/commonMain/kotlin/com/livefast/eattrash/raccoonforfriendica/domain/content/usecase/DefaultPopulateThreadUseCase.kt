package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.TimelineEntryModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.EmojiHelper
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.TimelineEntryRepository

private data class ConversationNode(val entry: TimelineEntryModel, var children: List<ConversationNode> = listOf())

internal class DefaultPopulateThreadUseCase(
    private val timelineEntryRepository: TimelineEntryRepository,
    private val emojiHelper: EmojiHelper,
) : PopulateThreadUseCase {
    override suspend fun invoke(entry: TimelineEntryModel, maxDepth: Int): List<TimelineEntryModel> {
        val root = ConversationNode(entry.original)
        populateTree(
            node = root,
            maxDepth = maxDepth,
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

    private suspend fun populateTree(node: ConversationNode, depth: Int, maxDepth: Int) {
        if (node.entry.original.replyCount == 0 || depth >= maxDepth) {
            return
        }
        val descendants =
            timelineEntryRepository
                .getContext(node.entry.id)
                ?.descendants
                .orEmpty()
                .map {
                    with(emojiHelper) { it.withEmojisIfMissing() }
                }
        buildTree(node = node, descendants = descendants, depth = depth, maxDepth)
    }

    private fun buildTree(node: ConversationNode, descendants: List<TimelineEntryModel>, depth: Int, maxDepth: Int) {
        if (node.entry.original.replyCount == 0 || depth >= maxDepth) {
            return
        }
        val children = descendants.filter {
            val referenceChild = it.original
            referenceChild.inReplyTo?.id == node.entry.id || referenceChild.inReplyTo?.id == node.entry.reblog?.id
        }
        node.children = children.map {
            ConversationNode(entry = it.original.copy(depth = node.entry.depth + 1))
        }
        for (child in node.children) {
            buildTree(node = child, descendants = descendants, depth = depth + 1, maxDepth)
        }
    }

    private fun MutableList<TimelineEntryModel>.linearize(node: ConversationNode) {
        add(node.entry)
        for (child in node.children) {
            linearize(child)
        }
    }

    private fun List<TimelineEntryModel>.deduplicate(): List<TimelineEntryModel> = distinctBy { it.safeKey }

    private fun List<TimelineEntryModel>.populateLoadMore() = mapIndexed { idx, entry ->
        val hasMoreReplies = entry.replyCount > 0
        val isNextCommentNotChild = idx == lastIndex || this[idx + 1].depth <= entry.depth
        entry.copy(loadMoreButtonVisible = hasMoreReplies && isNextCommentNotChild)
    }
}
