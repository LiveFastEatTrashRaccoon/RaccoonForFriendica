package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class DefaultSupportedFeatureRepository(private val nodeInfoRepository: NodeInfoRepository) :
    SupportedFeatureRepository {
    override val features = MutableStateFlow(NodeFeatures())

    override suspend fun refresh() {
        val info = nodeInfoRepository.getInfo()
        features.update {
            it.copy(
                supportsPhotoGallery = info.isFriendica,
                supportsDirectMessages = info.isFriendica,
                supportsEntryTitles = info.isFriendica,
                supportsCustomCircles = info.isFriendica,
                supportReportCategoryRuleViolation = !info.isFriendica,
                supportsPolls = !info.isFriendica,
                supportsBBCode = info.isFriendica,
                supportsMarkdown = true,
                supportsEntryShare = info.isFriendica,
                supportsCalendar = info.isFriendica,
                supportsAnnouncements = !info.isFriendica,
                supportsDislike = info.isFriendica,
                supportsTranslation = !info.isFriendica,
                supportsInlineImages = info.isFriendica,
                supportsLocalVisibility = info.isGoToSocial || info.isHomeTown,
            )
        }
    }
}

private val NodeInfoModel?.isFriendica: Boolean
    get() = this?.software?.lowercase() == "friendica"

private val NodeInfoModel?.isGoToSocial: Boolean
    get() = this?.software?.lowercase() == "gotosocial"

private val NodeInfoModel?.isHomeTown: Boolean
    get() = this?.software?.lowercase() == "hometown"
