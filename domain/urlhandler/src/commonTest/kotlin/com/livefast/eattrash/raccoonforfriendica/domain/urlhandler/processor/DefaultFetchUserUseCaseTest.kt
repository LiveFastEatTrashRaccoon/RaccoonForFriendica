package com.livefast.eattrash.raccoonforfriendica.domain.urlhandler.processor

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.ExploreItemModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SearchRepository
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class DefaultFetchUserUseCaseTest {
    private val searchRepository = mock<SearchRepository>()
    private val sut =
        DefaultFetchUserUseCase(
            searchRepository = searchRepository,
        )

    @Test
    fun `given user found when invoke then result is as expected`() = runTest {
        val user = UserModel(id = "0", username = USERNAME, url = URL)
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns
            listOf(ExploreItemModel.User(user))

        val res = sut.invoke(URL)

        assertNotNull(res)
        verifySuspend {
            searchRepository.search(
                query = URL,
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = true,
            )
        }
    }

    @Test
    fun `given user not found when invoke then result is as expected`() = runTest {
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } returns emptyList()

        val res = sut.invoke(URL)

        assertNull(res)
        verifySuspend {
            searchRepository.search(
                query = URL,
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = true,
            )
        }
    }

    @Test
    fun `given request timeout when invoke then result is as expected`() = runTest {
        everySuspend {
            searchRepository.search(
                query = any(),
                type = any(),
                pageCursor = any(),
                resolve = any(),
            )
        } calls {
            delay(10.seconds)
            listOf(
                ExploreItemModel.User(
                    UserModel(id = "0", username = USERNAME),
                ),
            )
        }

        val res = sut.invoke(URL)

        assertNull(res)
        verifySuspend {
            searchRepository.search(
                query = URL,
                type = SearchResultType.Users,
                pageCursor = null,
                resolve = true,
            )
        }
    }

    companion object {
        private const val URL = "https://example.com/profile/username"
        private const val USERNAME = "username"
    }
}
