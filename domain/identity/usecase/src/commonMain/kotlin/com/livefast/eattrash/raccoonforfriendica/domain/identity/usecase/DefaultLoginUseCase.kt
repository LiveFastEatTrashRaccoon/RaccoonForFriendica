package com.livefast.eattrash.raccoonforfriendica.domain.identity.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.repository.SupportedFeatureRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.AccountModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.SettingsModel
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountCredentialsCache
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.AccountRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiConfigurationRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.ApiCredentials
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.CredentialsRepository
import com.livefast.eattrash.raccoonforfriendica.domain.identity.repository.SettingsRepository

internal class DefaultLoginUseCase(
    private val apiConfigurationRepository: ApiConfigurationRepository,
    private val credentialsRepository: CredentialsRepository,
    private val accountRepository: AccountRepository,
    private val settingsRepository: SettingsRepository,
    private val accountCredentialsCache: AccountCredentialsCache,
    private val supportedFeatureRepository: SupportedFeatureRepository,
) : LoginUseCase {
    override suspend fun invoke(
        node: String,
        credentials: ApiCredentials,
    ) {
        apiConfigurationRepository.changeNode(node)
        apiConfigurationRepository.setAuth(credentials)
        val user = credentialsRepository.validate(node = node, credentials = credentials)
        checkNotNull(user) { "Invalid credentials" }

        val handle =
            buildString {
                append(user.username)
                append("@")
                append(node)
            }
        val oldAccount = accountRepository.getBy(handle = handle)
        if (oldAccount == null) {
            accountRepository.create(
                AccountModel(
                    handle = handle,
                    remoteId = user.id,
                    displayName = user.displayName,
                    avatar = user.avatar,
                ),
            )
        }

        accountRepository.getBy(handle)?.also { account ->
            accountCredentialsCache.save(accountId = account.id, credentials = credentials)

            val anonymousAccountId = accountRepository.getBy(handle = "")?.id ?: 0
            val oldSettings = settingsRepository.get(account.id)
            val defaultSettings = settingsRepository.get(anonymousAccountId) ?: SettingsModel()
            if (oldSettings == null) {
                supportedFeatureRepository.refresh()
                val supportsBBCode = supportedFeatureRepository.features.value.supportsBBCode
                settingsRepository.create(
                    defaultSettings.copy(
                        id = 0,
                        accountId = account.id,
                        // on Friendica, enable BBCode by default
                        markupMode =
                            if (supportsBBCode) {
                                MarkupMode.BBCode
                            } else {
                                MarkupMode.PlainText
                            },
                        // on Friendica, enable excludeRepliesFromTimeline
                        excludeRepliesFromTimeline = supportsBBCode,
                    ),
                )
            }

            accountRepository.setActive(account, true)
        }
    }
}
