package com.livefast.eattrash.raccoonforfriendica.domain.content.repository

import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeFeatures
import com.livefast.eattrash.raccoonforfriendica.domain.content.data.NodeInfoModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class DefaultSupportedFeatureRepository(private val nodeInfoRepository: NodeInfoRepository) :
    SupportedFeatureRepository {
    override val features = MutableStateFlow(NodeFeatures())

    override suspend fun refresh() {
        val info = nodeInfoRepository.getInfo()
        val isFriendica = info.isFriendica
        val isMastodonOrDerivative = !info.isFriendica
        val isMastodonExperimental = info.isGoToSocial || info.isHomeTown
        features.update {
            it.copy(
                supportsPhotoGallery = isFriendica,
                supportsDirectMessages = isFriendica,
                supportsEntryTitles = isFriendica,
                supportsCustomCircles = isFriendica,
                supportReportCategoryRuleViolation = isMastodonOrDerivative,
                supportsPolls = isMastodonOrDerivative,
                supportsBBCode = isFriendica,
                supportsMarkdown = true,
                supportsEntryShare = isFriendica,
                supportsCalendar = isFriendica,
                supportsAnnouncements = isMastodonOrDerivative,
                supportsDislike = isFriendica,
                supportsTranslation = isMastodonOrDerivative,
                supportsInlineImages = isFriendica,
                supportsLocalVisibility = isMastodonExperimental,
                supportsQuotePolicies = isMastodonOrDerivative,
                supportsCollections = isMastodonOrDerivative,
            )
        }
    }
}

private val NodeInfoModel?.isFriendica: Boolean
    get() = this?.software?.lowercase() == SoftwareNames.FRIENDICA ||
        this?.version?.lowercase()?.contains(SoftwareNames.FRIENDICA) == true

private val NodeInfoModel?.isGoToSocial: Boolean
    get() = this?.software?.lowercase() == SoftwareNames.GO_TO_SOCIAL

private val NodeInfoModel?.isHomeTown: Boolean
    get() = this?.software?.lowercase() == SoftwareNames.HOMETOWN

/**
 * Instance type names.
 */
private object SoftwareNames {
    const val FRIENDICA = "friendica"
    const val GO_TO_SOCIAL = "gotosocial"
    const val HOMETOWN = "hometown"
}
