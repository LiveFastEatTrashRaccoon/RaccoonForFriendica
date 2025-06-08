package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.UserRepository
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultExportUserListUseCaseTest {
    private val userRepository = mock<UserRepository>()
    private val sut =
        DefaultExportUserListUseCase(
            userRepository = userRepository,
        )

    @Test
    fun `given follower specification when invoke then result is as expected`() = runTest {
        val chunk1 =
            listOf(
                UserModel(id = "1", handle = "user1"),
                UserModel(id = "2", handle = "user2"),
            )
        val chunk2 =
            listOf(
                UserModel(id = "3", handle = "user3"),
                UserModel(id = "4", handle = "user4"),
            )
        everySuspend {
            userRepository.getFollowers(
                id = any(),
                pageCursor = any(),
            )
        } sequentiallyReturns listOf(chunk1, chunk2, emptyList())

        val expected =
            buildList {
                addAll(chunk1)
                addAll(chunk2)
            }.mapNotNull { it.handle }.joinToString("\n")

        val spec = ExportUserSpecification.Follower(userId = "0")
        val res = sut(spec)

        assertEquals(expected, res)
        verifySuspend(VerifyMode.exhaustiveOrder) {
            userRepository.getFollowers(
                id = "0",
                pageCursor = null,
            )
            userRepository.getFollowers(
                id = "0",
                pageCursor = "2",
            )
            userRepository.getFollowers(
                id = "0",
                pageCursor = "4",
            )
        }
    }

    @Test
    fun `given following specification when invoke then result is as expected`() = runTest {
        val chunk1 =
            listOf(
                UserModel(id = "1", handle = "user1"),
                UserModel(id = "2", handle = "user2"),
            )
        val chunk2 =
            listOf(
                UserModel(id = "3", handle = "user3"),
                UserModel(id = "4", handle = "user4"),
            )
        everySuspend {
            userRepository.getFollowing(
                id = any(),
                pageCursor = any(),
            )
        } sequentiallyReturns listOf(chunk1, chunk2, emptyList())

        val expected =
            buildList {
                addAll(chunk1)
                addAll(chunk2)
            }.mapNotNull { it.handle }.joinToString("\n")

        val spec = ExportUserSpecification.Following(userId = "0")
        val res = sut(spec)

        assertEquals(expected, res)
        verifySuspend(VerifyMode.exhaustiveOrder) {
            userRepository.getFollowing(
                id = "0",
                pageCursor = null,
            )
            userRepository.getFollowing(
                id = "0",
                pageCursor = "2",
            )
            userRepository.getFollowing(
                id = "0",
                pageCursor = "4",
            )
        }
    }
}
