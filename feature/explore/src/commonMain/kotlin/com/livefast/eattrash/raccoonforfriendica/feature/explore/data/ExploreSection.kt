package com.livefast.eattrash.raccoonforfriendica.feature.explore.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface ExploreSection {
    data object Posts : ExploreSection

    data object Hashtags : ExploreSection

    data object Links : ExploreSection

    data object Suggestions : ExploreSection
}

@Composable
fun ExploreSection.toReadableName(): String = when (this) {
    ExploreSection.Posts -> LocalStrings.current.accountSectionPosts
    ExploreSection.Hashtags -> LocalStrings.current.exploreSectionHashtags
    ExploreSection.Links -> LocalStrings.current.exploreSectionLinks
    ExploreSection.Suggestions -> LocalStrings.current.exploreSectionSuggestions
}
