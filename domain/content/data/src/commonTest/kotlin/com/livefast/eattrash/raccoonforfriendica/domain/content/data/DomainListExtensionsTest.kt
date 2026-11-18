package com.livefast.eattrash.raccoonforfriendica.domain.content.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DomainListExtensionsTest {

    private val post1 = PostModel(id = "1", content = "post 1")
    private val post2 = PostModel(id = "2", content = "post 2")
    private val reblogPost = PostModel(
        id = "3",
        content = "boost",
        reblog = PostModel(id = "4", content = "original post"),
    )

    @Test
    fun `given matching direct post id when updateMatching then post is updated`() {
        val list = listOf(post1, post2)

        val result = list.updateMatching("1") { it.copy(content = "updated") }

        assertEquals("updated", result.first { it.id == "1" }.content)
        assertEquals("post 2", result.first { it.id == "2" }.content)
    }

    @Test
    fun `given matching reblog id when updateMatching then reblog is updated`() {
        val list = listOf(post1, reblogPost)

        val result = list.updateMatching("4") { it.copy(content = "updated original") }

        val boost = result.first { it.id == "3" }
        assertEquals("boost", boost.content)
        assertEquals("updated original", boost.reblog?.content)
    }

    @Test
    fun `given no matching id when updateMatching then list is unchanged`() {
        val list = listOf(post1, post2)

        val result = list.updateMatching("99") { it.copy(content = "updated") }

        assertEquals(list, result)
    }

    @Test
    fun `given matching direct post id when removeMatching then post is removed`() {
        val list = listOf(post1, post2)

        val result = list.removeMatching("1")

        assertEquals(listOf(post2), result)
    }

    @Test
    fun `given matching reblog id when removeMatching then reblog post is removed`() {
        val list = listOf(post1, reblogPost)

        val result = list.removeMatching("4")

        assertEquals(listOf(post1), result)
    }

    @Test
    fun `given no matching id when removeMatching then list is unchanged`() {
        val list = listOf(post1, post2)

        val result = list.removeMatching("99")

        assertEquals(list, result)
    }

    @Test
    fun `given explore item post when updateMatching then post is updated`() {
        val explorePost = ExploreItemModel.Post(post = post1)
        val exploreUser = ExploreItemModel.User(user = UserModel(id = "u1"))
        val list: List<ExploreItemModel> = listOf(explorePost, exploreUser)

        val result = list.updateMatching("1") { it.copy(content = "updated") }

        val updatedPost = (result[0] as ExploreItemModel.Post).post
        assertEquals("updated", updatedPost.content)
        assertEquals(exploreUser, result[1])
    }

    @Test
    fun `given explore item post when removeMatching then post is removed`() {
        val explorePost = ExploreItemModel.Post(post = post1)
        val exploreUser = ExploreItemModel.User(user = UserModel(id = "u1"))
        val list: List<ExploreItemModel> = listOf(explorePost, exploreUser)

        val result = list.removeMatching("1")

        assertEquals(listOf(exploreUser), result)
    }

    @Test
    fun `given 2D post list when updateMatching at index then only target index is updated`() {
        val page0 = listOf(post1)
        val page1 = listOf(post2)
        val list2d = listOf(page0, page1)

        val result = list2d.updateMatching(index = 1, id = "2") { it.copy(content = "updated 2") }

        assertEquals(page0, result[0])
        assertEquals("updated 2", result[1][0].content)
    }

    @Test
    fun `given 2D post list when removeMatching at index then only target index is removed`() {
        val page0 = listOf(post1)
        val page1 = listOf(post2)
        val list2d = listOf(page0, page1)

        val result = list2d.removeMatching(index = 0, id = "1")

        assertTrue(result[0].isEmpty())
        assertEquals(page1, result[1])
    }
}
