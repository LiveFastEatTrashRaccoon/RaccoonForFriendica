package com.livefast.eattrash.raccoonforfriendica.core.api.service

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Relationship
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Status
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Suggestion
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import io.ktor.client.HttpClient
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

internal class DefaultUserService(private val baseUrl: String, private val client: HttpClient) : UserService {
    override suspend fun verifyCredentials(): CredentialAccount =
        client.get("$baseUrl/v1/accounts/verify_credentials").body()

    override suspend fun getById(id: String): Account = client.get("$baseUrl/v1/accounts/$id").body()

    override suspend fun search(query: String, offset: Int, resolve: Boolean, following: Boolean): List<Account> =
        client.get("$baseUrl/v1/accounts/search") {
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
    ): List<Status> = client.get("$baseUrl/v1/accounts/$id/statuses") {
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
        client.get("$baseUrl/v1/accounts/relationships") {
            id.forEach { value ->
                parameter("id[]", value)
            }
        }.body()

    override suspend fun getSuggestions(limit: Int): List<Suggestion> = client.get("$baseUrl/v1/accounts/suggestions") {
        parameter("limit", limit)
    }.body()

    override suspend fun getFollowers(id: String, maxId: String?, minId: String?, limit: Int): List<Account> =
        client.get("$baseUrl/v1/accounts/$id/followers") {
            parameter("id", id)
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun getFollowing(id: String, maxId: String?, minId: String?, limit: Int): List<Account> =
        client.get("$baseUrl/v1/accounts/$id/following") {
            parameter("id", id)
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun follow(id: String, data: FollowUserForm): Relationship =
        client.post("$baseUrl/v1/accounts/$id/follow") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun unfollow(id: String): Relationship = client.post("$baseUrl/v1/accounts/$id/unfollow").body()

    override suspend fun getFavorites(maxId: String?, minId: String?, limit: Int): List<Status> =
        client.get("$baseUrl/v1/favourites") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun getBookmarks(maxId: String?, minId: String?, limit: Int): List<Status> =
        client.get("$baseUrl/v1/bookmarks") {
            parameter("max_id", maxId)
            parameter("min_id", minId)
            parameter("limit", limit)
        }.body()

    override suspend fun mute(id: String, data: MuteUserForm): Relationship =
        client.post("$baseUrl/v1/accounts/$id/mute") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()

    override suspend fun unmute(id: String): Relationship = client.post("$baseUrl/v1/accounts/$id/unmute").body()

    override suspend fun block(id: String): Relationship = client.post("$baseUrl/v1/accounts/$id/block").body()

    override suspend fun unblock(id: String): Relationship = client.post("$baseUrl/v1/accounts/$id/unblock").body()

    override suspend fun getMuted(maxId: String?, limit: Int): List<Account> = client.get("$baseUrl/v1/mutes") {
        parameter("max_id", maxId)
        parameter("limit", limit)
    }.body()

    override suspend fun getBlocked(maxId: String?, limit: Int): List<Account> = client.get("$baseUrl/v1/blocks") {
        parameter("max_id", maxId)
        parameter("limit", limit)
    }.body()

    override suspend fun updateProfile(content: FormDataContent): Account =
        client.patch("$baseUrl/v1/accounts/update_credentials") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun updateProfileImage(content: MultiPartFormDataContent): Account =
        client.patch("$baseUrl/v1/accounts/update_credentials") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.body()

    override suspend fun updatePersonalNote(id: String, data: FormDataContent): Relationship =
        client.post("$baseUrl/v1/accounts/$id/note") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
}
