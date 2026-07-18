package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.serialName
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.CircleModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.QuotePolicy
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.SearchResultType
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.ListWithPageCursor
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toDto
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.toModel
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultUserRepository(
    private val provider: ServiceProvider,
    private val otherProvider: ServiceProvider,
) : UserRepository {
    private val otherMutex = Mutex()
    private var cachedUser: UserModel? = null

    override suspend fun getById(id: String): UserModel? = try {
        provider.user.getById(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun search(query: String, offset: Int, following: Boolean): List<UserModel>? = try {
        provider.user
            .search(
                query = query,
                offset = offset,
                resolve = true,
                following = following,
            ).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getByHandle(handle: String): UserModel? = try {
        provider.user
            .search(
                query = handle,
                resolve = true,
            ).first()
            .toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getCurrent(refresh: Boolean): UserModel? {
        if (refresh) {
            cachedUser = null
        }
        val fromCache = cachedUser
        check(fromCache == null) { return fromCache }
        return try {
            provider.user.verifyCredentials().toModel()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            null
        }.also {
            cachedUser = it
        }
    }

    override suspend fun getRelationships(ids: List<String>): List<RelationshipModel>? = try {
        provider.user
            .getRelationships(ids)
            .map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getSuggestions(): List<UserModel>? = try {
        provider.user
            .getSuggestions(
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.user.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getFollowers(id: String, pageCursor: String?, otherInstance: String?): List<UserModel>? = try {
        withProvider(otherInstance) { provider ->
            provider.user
                .getFollowers(
                    id = id,
                    maxId = pageCursor,
                    limit = DEFAULT_PAGE_SIZE,
                ).map { it.toModel() }
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getFollowing(id: String, pageCursor: String?, otherInstance: String?): List<UserModel>? = try {
        withProvider(otherInstance) { provider ->
            provider.user
                .getFollowing(
                    id = id,
                    maxId = pageCursor,
                    limit = DEFAULT_PAGE_SIZE,
                ).map { it.toModel() }
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getListsContaining(id: String): List<CircleModel>? = try {
        provider.user.getListsContaining(id).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun searchMyFollowing(query: String, pageCursor: String?): List<UserModel>? = try {
        provider.search
            .search(
                query = query,
                maxId = pageCursor,
                type = SearchResultType.Users.toDto(),
                following = true,
                limit = DEFAULT_PAGE_SIZE,
            ).accounts
            .map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun follow(id: String, reblogs: Boolean, notifications: Boolean): RelationshipModel? = try {
        val data =
            FollowUserForm(
                reblogs = reblogs,
                notify = notifications,
            )
        provider.user
            .follow(
                id = id,
                data = data,
            ).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun unfollow(id: String): RelationshipModel? = try {
        provider.user.unfollow(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getFollowRequests(pageCursor: String?): ListWithPageCursor<UserModel>? = try {
        val (list, cursor) =
            provider.followRequest.getAll(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            )
        ListWithPageCursor(list = list.map { it.toModel() }, cursor = cursor)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun acceptFollowRequest(id: String) = try {
        provider.followRequest.accept(id)
        true
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        false
    }

    override suspend fun rejectFollowRequest(id: String) = try {
        provider.followRequest.reject(id)
        true
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        false
    }

    override suspend fun mute(id: String, durationSeconds: Long, notifications: Boolean): RelationshipModel? = try {
        val data =
            MuteUserForm(
                duration = durationSeconds,
                notifications = notifications,
            )
        provider.user
            .mute(
                id = id,
                data = data,
            ).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun unmute(id: String): RelationshipModel? = try {
        provider.user.unmute(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun block(id: String): RelationshipModel? = try {
        provider.user.block(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun unblock(id: String): RelationshipModel? = try {
        provider.user.unblock(id).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getMuted(pageCursor: String?): List<UserModel>? = try {
        provider.user
            .getMuted(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun getBlocked(pageCursor: String?): List<UserModel>? = try {
        provider.user
            .getBlocked(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun updateProfile(
        note: String?,
        displayName: String?,
        avatar: ByteArray?,
        header: ByteArray?,
        locked: Boolean?,
        bot: Boolean?,
        discoverable: Boolean?,
        hideCollections: Boolean?,
        indexable: Boolean?,
        quotePolicy: QuotePolicy?,
        fields: Map<String, String>?,
    ) = try {
        var result: Account

        // textual data
        val formData =
            FormDataContent(
                parameters {
                    if (note != null) {
                        append("note", note)
                    }
                    if (displayName != null) {
                        append("display_name", displayName)
                    }
                    if (locked != null) {
                        append("locked", if (locked) "1" else "0")
                    }
                    if (bot != null) {
                        append("bot", if (bot) "1" else "0")
                    }
                    if (discoverable != null) {
                        append("discoverable", if (discoverable) "1" else "0")
                    }
                    if (hideCollections != null) {
                        append("hide_collections", if (hideCollections) "1" else "0")
                    }
                    if (indexable != null) {
                        append("indexable", if (indexable) "1" else "0")
                    }
                    if (quotePolicy != null) {
                        append("source[quote_policy]", quotePolicy.toDto().serialName)
                    }
                    if (fields != null) {
                        val serializedFields =
                            buildList {
                                fields.entries.forEachIndexed { idx, entry ->
                                    val nameKey = "fields_attributes[$idx][name]"
                                    val nameValue = entry.key
                                    this += (nameKey to nameValue)
                                    val valueKey = "fields_attributes[$idx][value]"
                                    val valueValue = entry.value
                                    this += (valueKey to valueValue)
                                }
                            }
                        for (field in serializedFields) {
                            append(field.first, field.second)
                        }
                    }
                },
            )
        result = provider.user.updateProfile(formData)

        // images
        if (avatar != null) {
            val multipartFormData =
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "avatar",
                            value = avatar,
                            headers =
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/*")
                                append(HttpHeaders.ContentDisposition, "filename=avatar.jpeg")
                            },
                        )
                    },
                )
            result = provider.user.updateProfileImage(multipartFormData)
        }

        if (header != null) {
            val multipartFormData =
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "header",
                            value = header,
                            headers =
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/*")
                                append(HttpHeaders.ContentDisposition, "filename=header.jpeg")
                            },
                        )
                    },
                )
            result = provider.user.updateProfileImage(multipartFormData)
        }

        result.toModel().also {
            // refresh cached value, just in case
            cachedUser = it
        }
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    override suspend fun updatePersonalNote(id: String, value: String): RelationshipModel? = try {
        val data =
            FormDataContent(
                parameters {
                    append("comment", value)
                },
            )
        provider.user.updatePersonalNote(id = id, data = data).toModel()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        null
    }

    private suspend fun <T> withProvider(otherInstance: String?, block: suspend (ServiceProvider) -> T): T =
        if (otherInstance.isNullOrEmpty()) {
            block(provider)
        } else {
            otherMutex.withLock {
                otherProvider.changeNode(otherInstance)
                block(otherProvider)
            }
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
