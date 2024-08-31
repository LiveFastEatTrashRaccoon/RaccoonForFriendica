package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.form.FollowUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.form.MuteUserForm
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.RelationshipModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.utils.extractNextIdFromResponseLinkHeader
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
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

    override suspend fun getCurrent(): UserModel? =
        withContext(Dispatchers.IO) {
            runCatching {
                provider.users.verifyCredentials().toModel()
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

    override suspend fun searchMyFollowing(
        query: String,
        pageCursor: String?,
    ): List<UserModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                withContext(Dispatchers.IO) {
                    provider.search
                        .search(
                            query = query,
                            maxId = pageCursor,
                            type = "accounts",
                            limit = DEFAULT_PAGE_SIZE,
                        ).accounts
                        .map { it.toModel() }
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
            result = provider.users.updateProfile(formData)

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
                result = provider.users.updateProfileImage(multipartFormData)
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
                result = provider.users.updateProfileImage(multipartFormData)
            }

            result.toModel()
        }.getOrNull()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
