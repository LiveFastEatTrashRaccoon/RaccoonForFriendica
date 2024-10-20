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
                supportsPhotoGallery = info?.isFriendica == true,
                supportsDirectMessages = info?.isFriendica == true,
                supportsEntryTitles = info?.isFriendica == true,
                supportsCustomCircles = info?.isFriendica == true,
                supportReportCategoryRuleViolation = info?.isFriendica == false,
                supportsPolls = info?.isFriendica == false,
                supportsBBCode = info?.isFriendica == true,
                supportsMarkdown = info?.isFriendica == false,
                supportsEntryShare = info?.isFriendica == true,
                supportsPrivateVisibility = info?.isFriendica == true,
                supportsCalendar = info?.isFriendica == true,
            )
        }
    }
}

private val FRIENDICA_REGEX =
    Regex("\\(compatible; Friendica (?<version>[a-zA-Z0-9.-_]*)\\)")

private val NodeInfoModel.isFriendica: Boolean
    get() =
        version
            .orEmpty()
            .contains(FRIENDICA_REGEX)
