package com.livefast.eattrash.raccoonforfriendica.domain.identity.repository

import com.livefast.eattrash.raccoonforfriendica.core.api.dto.Account
import com.livefast.eattrash.raccoonforfriendica.core.api.dto.CredentialAccount
import com.livefast.eattrash.raccoonforfriendica.core.api.provider.ServiceProvider
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.FieldModel
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.UserModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultIdentityRepository(private val provider: ServiceProvider) : IdentityRepository {
    override val currentUser = MutableStateFlow<UserModel?>(null)

    override suspend fun refreshCurrentUser(userId: String?) {
        if (userId.isNullOrEmpty()) {
            currentUser.update { null }
        } else {
            try {
                val user = provider.user.getById(userId)
                updateCurrentUser(user)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                try {
                    val user = provider.user.verifyCredentials()
                    updateCurrentUser(user)
                } catch (e2: Exception) {
                    if (e2 is CancellationException) throw e2
                    currentUser.update { null }
                }
            }
        }
    }

    private fun updateCurrentUser(user: CredentialAccount) {
        currentUser.update {
            UserModel(
                avatar = user.avatar,
                bio = user.note,
                created = user.createdAt,
                displayName = user.displayName,
                entryCount = user.statusesCount,
                fields =
                user.fields.map {
                    FieldModel(
                        key = it.name,
                        value = it.value,
                        verified = it.verifiedAt != null,
                    )
                },
                followers = user.followersCount,
                following = user.followingCount,
                group = user.group,
                handle = user.acct,
                header = user.header,
                id = user.id,
                url = user.url,
                username = user.username,
            )
        }
    }

    private fun updateCurrentUser(user: Account) {
        currentUser.update {
            UserModel(
                avatar = user.avatar,
                bio = user.note,
                created = user.createdAt,
                displayName = user.displayName,
                entryCount = user.statusesCount,
                fields =
                user.fields.map {
                    FieldModel(
                        key = it.name,
                        value = it.value,
                        verified = it.verifiedAt != null,
                    )
                },
                followers = user.followersCount,
                following = user.followingCount,
                group = user.group,
                handle = user.acct,
                header = user.header,
                id = user.id,
                url = user.url,
                username = user.username,
            )
        }
    }
}
