package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
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

internal class DefaultUserRepository(private val provider: ServiceProvider) : UserRepository {
    private var cachedUser: UserModel? = null

    override suspend fun getById(id: String): UserModel? = runCatching {
        provider.user.getById(id).toModel()
    }.getOrNull()

    override suspend fun search(query: String, offset: Int, following: Boolean): List<UserModel>? = runCatching {
        provider.user
            .search(
                query = query,
                offset = offset,
                resolve = true,
                following = following,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun getByHandle(handle: String): UserModel? = runCatching {
        provider.user
            .search(
                query = handle,
                resolve = true,
            ).first()
            .toModel()
    }.getOrNull()

    override suspend fun getCurrent(refresh: Boolean): UserModel? {
        if (refresh) {
            cachedUser = null
        }
        val fromCache = cachedUser
        check(fromCache == null) { return fromCache }
        return runCatching {
            provider.user.verifyCredentials().toModel()
        }.getOrNull().also {
            cachedUser = it
        }
    }

    override suspend fun getRelationships(ids: List<String>): List<RelationshipModel>? = runCatching {
        provider.user
            .getRelationships(ids)
            .map { it.toModel() }
    }.getOrNull()

    override suspend fun getSuggestions(): List<UserModel>? = runCatching {
        provider.user
            .getSuggestions(
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.user.toModel() }
    }.getOrNull()

    override suspend fun getFollowers(id: String, pageCursor: String?): List<UserModel>? = runCatching {
        provider.user
            .getFollowers(
                id = id,
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun getFollowing(id: String, pageCursor: String?): List<UserModel>? = runCatching {
        provider.user
            .getFollowing(
                id = id,
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun searchMyFollowing(query: String, pageCursor: String?): List<UserModel>? = runCatching {
        provider.search
            .search(
                query = query,
                maxId = pageCursor,
                type = SearchResultType.Users.toDto(),
                following = true,
                limit = DEFAULT_PAGE_SIZE,
            ).accounts
            .map { it.toModel() }
    }.getOrNull()

    override suspend fun follow(id: String, reblogs: Boolean, notifications: Boolean): RelationshipModel? =
        runCatching {
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
        }.getOrNull()

    override suspend fun unfollow(id: String): RelationshipModel? = runCatching {
        provider.user.unfollow(id).toModel()
    }.getOrNull()

    override suspend fun getFollowRequests(pageCursor: String?): ListWithPageCursor<UserModel>? = runCatching {
        val (list, cursor) =
            provider.followRequest.getAll(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            )
        ListWithPageCursor(list = list.map { it.toModel() }, cursor = cursor)
    }.getOrNull()

    override suspend fun acceptFollowRequest(id: String) = runCatching {
        provider.followRequest.accept(id)
        true
    }.getOrElse { false }

    override suspend fun rejectFollowRequest(id: String) = runCatching {
        provider.followRequest.reject(id)
        true
    }.getOrElse { false }

    override suspend fun mute(id: String, durationSeconds: Long, notifications: Boolean): RelationshipModel? =
        runCatching {
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
        }.getOrNull()

    override suspend fun unmute(id: String): RelationshipModel? = runCatching {
        provider.user.unmute(id).toModel()
    }.getOrNull()

    override suspend fun block(id: String): RelationshipModel? = runCatching {
        provider.user.block(id).toModel()
    }.getOrNull()

    override suspend fun unblock(id: String): RelationshipModel? = runCatching {
        provider.user.unblock(id).toModel()
    }.getOrNull()

    override suspend fun getMuted(pageCursor: String?): List<UserModel>? = runCatching {
        provider.user
            .getMuted(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

    override suspend fun getBlocked(pageCursor: String?): List<UserModel>? = runCatching {
        provider.user
            .getBlocked(
                maxId = pageCursor,
                limit = DEFAULT_PAGE_SIZE,
            ).map { it.toModel() }
    }.getOrNull()

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
        fields: Map<String, String>?,
    ) = runCatching {
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

        result.toModel()
    }.getOrNull()

    override suspend fun updatePersonalNote(id: String, value: String): RelationshipModel? = runCatching {
        val data =
            FormDataContent(
                parameters {
                    append("comment", value)
                },
            )
        provider.user.updatePersonalNote(id = id, data = data).toModel()
    }.getOrNull()

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
