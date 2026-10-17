package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Collections
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Page
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Suggestion
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.UserList
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceCreationArgs
import com.livefast.eattrash.raccoonforfriendica.core.api.utils.extractCursorFromLinkHeaderValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

@AssistedInject
class DefaultUserService(@Assisted private val args: ServiceCreationArgs) : UserService {

    override suspend fun verifyCredentials(): CredentialAccount =
        args.client.get("${args.baseUrl}/v1/accounts/verify_credentials").body()

    override suspend fun getById(id: String): Account = args.client.get("${args.baseUrl}/v1/accounts/$id").body()

    override suspend fun search(query: String, offset: Int, resolve: Boolean, following: Boolean): List<Account> =
        args.client.get("${args.baseUrl}/v1/accounts/search") {
            parameter("q", query)
            parameter("offset", offset)
            parameter("resolve", resolve)
            parameter("following", following)
        }.body()

    override suspend fun getStatuses(
        id: String,
        maxId: String?,
        minId: String?,
        onlyMedia: Boolean,
        excludeReplies: Boolean,
        excludeReblogs: Boolean,
        pinned: Boolean,
        limit: Int,
    ): List<Status> = args.client.get("${args.baseUrl}/v1/accounts/$id/statuses") {
        parameter("id", id)
        parameter("max_id", maxId)
        parameter("min_id", minId)
        parameter("only_media", onlyMedia)
        parameter("exclude_replies", excludeReplies)
        parameter("exclude_reblogs", excludeReblogs)
        parameter("pinned", pinned)
        parameter("limit", limit)
    }.body()

    override suspend fun getRelationships(id: List<String>): List<Relationship> =
        args.client.get("${args.baseUrl}/v1/accounts/relationships") {
            id.forEach { value ->
                parameter("id[]", value)
            }
        }.body()

    override suspend fun getSuggestions(limit: Int): List<Suggestion> =
        args.client.get("${args.baseUrl}/v2/suggestions") {
            parameter("limit", limit)
        }.body()

    override suspend fun getFollowers(id: String, maxId: String?, minId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/accounts/$id/followers") {
            parameter("id", id)
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        val data: List<Account> = response.body()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun getFollowing(id: String, maxId: String?, minId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/accounts/$id/following") {
            parameter("id", id)
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        val data: List<Account> = response.body()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun follow(id: String, data: FollowUserForm): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/follow") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun unfollow(id: String): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/unfollow").body()

    override suspend fun getFavorites(maxId: String?, minId: String?, limit: Int): Page<Status> {
        val response = args.client.get("${args.baseUrl}/v1/favourites") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        val data: List<Status> = response.body()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun getBookmarks(maxId: String?, minId: String?, limit: Int): Page<Status> {
        val response = args.client.get("${args.baseUrl}/v1/bookmarks") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        val data: List<Status> = response.body()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun getListsContaining(id: String): List<UserList> =
        args.client.get("${args.baseUrl}/v1/accounts/$id/lists").body()

    override suspend fun mute(id: String, data: MuteUserForm): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/mute") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun unmute(id: String): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/unmute").body()

    override suspend fun block(id: String): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/block").body()

    override suspend fun unblock(id: String): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/unblock").body()

    override suspend fun getMuted(maxId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/mutes") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        val data: List<Account> = response.body()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun getBlocked(maxId: String?, limit: Int): Page<Account> {
        val response = args.client.get("${args.baseUrl}/v1/blocks") {
            parameter("max_id", maxId)
            parameter("limit", limit)
        }
        val cursor = response.headers["link"]?.extractCursorFromLinkHeaderValue()
        val data: List<Account> = response.body()
        return Page(elements = data, cursor = cursor)
    }

    override suspend fun updateProfile(content: FormDataContent): Account =
        args.client.patch("${args.baseUrl}/v1/accounts/update_credentials") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun updateProfileImage(content: MultiPartFormDataContent): Account =
        args.client.patch("${args.baseUrl}/v1/accounts/update_credentials") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun updatePersonalNote(id: String, data: FormDataContent): Relationship =
        args.client.post("${args.baseUrl}/v1/accounts/$id/note") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun getCollections(id: String, offset: Int, limit: Int): Collections {
        val response = args.client.get("${args.baseUrl}/v1/accounts/$id/collections") {
            parameter("offset", offset)
            parameter("limit", limit)
        }
        return response.body<Collections>()
    }

    override suspend fun getCollectionsContaining(id: String, offset: Int, limit: Int): Collections {
        val response = args.client.get("${args.baseUrl}/v1/accounts/$id/in_collections") {
            parameter("offset", offset)
            parameter("limit", limit)
        }
        return response.body<Collections>()
    }
}

@ContributesBinding(AppScope::class)
@AssistedFactory
fun interface DefaultUserServiceFactory : UserServiceFactory {
    override fun create(@Assisted args: ServiceCreationArgs): DefaultUserService
}
