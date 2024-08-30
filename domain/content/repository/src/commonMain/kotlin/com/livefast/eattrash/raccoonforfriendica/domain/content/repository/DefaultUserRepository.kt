package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.extractNextIdFromResponseLinkHeader
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultUserRepository(
    private val provider: ServiceProvider,
) : UserRepository {
    override suspend fun getById(id: String): UserModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users.getById(id).toModel()
                }
            }.getOrNull()
        }

    override suspend fun search(
        query: String,
        offset: Int,
    ): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .search(
                            query = query,
                            offset = offset,
                        ).map { it.toModel() }
                }
            }.getOrElse { emptyList() }
        }

    override suspend fun getByHandle(handle: String): UserModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .search(
                            query = handle,
                            resolve = true,
                        ).first()
                        .toModel()
                }
            }.getOrNull()
        }

    override suspend fun getRelationships(ids: List<String>): List<RelationshipModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .getRelationships(ids)
                        .map { it.toModel() }
                }
            }.getOrElse { emptyList() }
        }

    override suspend fun getSuggestions(): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .getSuggestions(
                            limit = DEFAULT_PAGE_SIZE,
                        ).map { it.user.toModel() }
                }
            }.apply {
                exceptionOrNull()?.also {
                    it.printStackTrace()
                }
            }.getOrElse { emptyList() }
        }

    override suspend fun getFollowers(
        id: String,
        pageCursor: String?,
    ): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .getFollowers(
                            id = id,
                            maxId = pageCursor,
                            limit = DEFAULT_PAGE_SIZE,
                        ).map { it.toModel() }
                }
            }.getOrElse { emptyList() }
        }

    override suspend fun getFollowing(
        id: String,
        pageCursor: String?,
    ): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .getFollowing(
                            id = id,
                            maxId = pageCursor,
                            limit = DEFAULT_PAGE_SIZE,
                        ).map { it.toModel() }
                }
            }.getOrElse { emptyList() }
        }

    override suspend fun follow(
        id: String,
        reblogs: Boolean,
        notifications: Boolean,
    ): RelationshipModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    val data =
                        FollowUserForm(
                            reblogs = reblogs,
                            notify = notifications,
                        )
                    provider.users
                        .follow(
                            id = id,
                            data = data,
                        ).toModel()
                }
            }.getOrNull()
        }

    override suspend fun unfollow(id: String): RelationshipModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users.unfollow(id).toModel()
                }
            }.getOrNull()
        }

    override suspend fun getFollowRequests(pageCursor: String?): Pair<List<UserModel>, String?> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    val response =
                        provider.followRequests.getAll(
                            maxId = pageCursor,
                            limit = DEFAULT_PAGE_SIZE,
                        )
                    val list: List<UserModel> = response.body()?.map { it.toModel() }.orEmpty()
                    val nextCursor: String? = response.extractNextIdFromResponseLinkHeader()
                    list to nextCursor
                }
            }.getOrElse { emptyList<UserModel>() to null }
        }

    override suspend fun acceptFollowRequest(id: String) =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.followRequests.accept(id)
                    true
                }
            }.getOrElse { false }
        }

    override suspend fun rejectFollowRequest(id: String) =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.followRequests.reject(id)
                    true
                }
            }.getOrElse { false }
        }

    override suspend fun mute(
        id: String,
        durationSeconds: Long,
        notifications: Boolean,
    ): RelationshipModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    val data =
                        MuteUserForm(
                            duration = durationSeconds,
                            notifications = notifications,
                        )
                    provider.users
                        .mute(
                            id = id,
                            data = data,
                        ).toModel()
                }
            }.getOrNull()
        }

    override suspend fun unmute(id: String): RelationshipModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users.unmute(id).toModel()
                }
            }.getOrNull()
        }

    override suspend fun block(id: String): RelationshipModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users.block(id).toModel()
                }
            }.getOrNull()
        }

    override suspend fun unblock(id: String): RelationshipModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users.unblock(id).toModel()
                }
            }.getOrNull()
        }

    override suspend fun getMuted(pageCursor: String?): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .getMuted(
                            maxId = pageCursor,
                            limit = DEFAULT_PAGE_SIZE,
                        ).map { it.toModel() }
                }
            }.getOrElse { emptyList() }
        }

    override suspend fun getBlocked(pageCursor: String?): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.users
                        .getBlocked(
                            maxId = pageCursor,
                            limit = DEFAULT_PAGE_SIZE,
                        ).map { it.toModel() }
                }
            }.getOrElse { emptyList() }
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
        fields: Map<String, String>?,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val content =
                MultiPartFormDataContent(
                    formData {
                        if (note != null) {
                            append("note", note)
                        }
                        if (displayName != null) {
                            append("display_name", displayName)
                        }
                        if (locked != null) {
                            append("locked", locked)
                        }
                        if (bot != null) {
                            append("bot", bot)
                        }
                        if (discoverable != null) {
                            append("discoverable", discoverable)
                        }
                        if (hideCollections != null) {
                            append("hide_collections", hideCollections)
                        }
                        if (fields != null) {
                            val encodedMap = mutableMapOf<Int, Map<String, String>>()
                            fields.entries.forEachIndexed { idx, entry ->
                                encodedMap[idx] =
                                    mapOf(
                                        "name" to entry.key,
                                        "value" to entry.value,
                                    )
                            }
                            append("fields_attributes", encodedMap.toString())
                        }
                        if (avatar != null) {
                            append(
                                key = "avatar",
                                value = avatar,
                                headers =
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/*")
                                        append(HttpHeaders.ContentDisposition, "filename=avatar.jpeg")
                                    },
                            )
                        }
                        if (header != null) {
                            append(
                                key = "header",
                                value = header,
                                headers =
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/*")
                                        append(HttpHeaders.ContentDisposition, "filename=header.jpeg")
                                    },
                            )
                        }
                    },
                )
            provider.users.updateProfile(content).toModel()
        }.getOrNull()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
