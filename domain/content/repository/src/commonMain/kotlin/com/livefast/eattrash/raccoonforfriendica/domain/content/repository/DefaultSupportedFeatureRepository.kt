package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultSupportedFeatureRepository(
    private val nodeInfoRepository: NodeInfoRepository,
) : SupportedFeatureRepository {
    override val features = MutableStateFlow(NodeFeatures())

    override suspend fun refresh() {
        val info = nodeInfoRepository.getInfo()
        features.update {
            it.copy(
                supportsPhotoGallery = info.isFriendica,
                supportsDirectMessages = info.isFriendica,
                supportsEntryTitles = info.isFriendica,
                supportsCustomCircles = info.isFriendica,
                supportReportCategoryRuleViolation = info.isMastodon,
                supportsPolls = info.isMastodon,
                supportsBBCode = info.isFriendica,
                supportsMarkdown = true,
                supportsEntryShare = info.isFriendica,
                supportsCalendar = info.isFriendica,
                supportsAnnouncements = info.isMastodon,
                supportsDislike = info.isFriendica,
                supportsTranslation = info.isMastodon,
            )
        }
    }
}

private val FRIENDICA_REGEX =
    Regex("\\(compatible; Friendica (?<version>[a-zA-Z0-9.\\-_]*)\\)")

private val NodeInfoModel?.isFriendica: Boolean
    get() = this?.version?.contains(FRIENDICA_REGEX) ?: false

private val NodeInfoModel?.isMastodon: Boolean
    get() = !isFriendica
