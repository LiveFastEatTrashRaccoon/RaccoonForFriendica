package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.PostModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.original
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.safeKey
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.PostRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

private data class ConversationNode(val post: PostModel, var children: List<ConversationNode> = listOf())

@ContributesBinding(AppScope::class)
@Inject
class DefaultPopulateThreadUseCase(private val postRepository: PostRepository) : PopulateThreadUseCase {
    override suspend fun invoke(post: PostModel, maxDepth: Int): List<PostModel> {
        val root = ConversationNode(post.original)
        populateTree(
            node = root,
            maxDepth = maxDepth,
            depth = 0,
        )
        val res =
            mutableListOf<PostModel>()
                .apply {
                    linearize(root)
                }.deduplicate()
                .populateLoadMore()
        return res.toList()
    }

    private suspend fun populateTree(node: ConversationNode, depth: Int, maxDepth: Int) {
        if (node.post.original.replyCount == 0 || depth >= maxDepth) {
            return
        }
        val descendants =
            postRepository
                .getContext(node.post.id)
                ?.descendants
                .orEmpty()
        buildTree(node = node, descendants = descendants, depth = depth, maxDepth)
    }

    private fun buildTree(node: ConversationNode, descendants: List<PostModel>, depth: Int, maxDepth: Int) {
        if (node.post.original.replyCount == 0 || depth >= maxDepth) {
            return
        }
        val children = descendants.filter {
            val referenceChild = it.original
            referenceChild.inReplyTo?.id == node.post.id || referenceChild.inReplyTo?.id == node.post.reblog?.id
        }
        node.children = children.map {
            ConversationNode(post = it.original.copy(depth = node.post.depth + 1))
        }
        for (child in node.children) {
            buildTree(node = child, descendants = descendants, depth = depth + 1, maxDepth)
        }
    }

    private fun MutableList<PostModel>.linearize(node: ConversationNode) {
        add(node.post)
        for (child in node.children) {
            linearize(child)
        }
    }

    private fun List<PostModel>.deduplicate(): List<PostModel> = distinctBy { it.safeKey }

    private fun List<PostModel>.populateLoadMore() = mapIndexed { idx, post ->
        val hasMoreReplies = post.replyCount > 0
        val isNextCommentNotChild = idx == lastIndex || this[idx + 1].depth <= post.depth
        post.copy(loadMoreButtonVisible = hasMoreReplies && isNextCommentNotChild)
    }
}
