package com.livefast.eattrash.raccoonforfriendica.feature.explore.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface ExploreSection {
    data object Posts : ExploreSection

    data object Hashtags : ExploreSection

    data object Links : ExploreSection

    data object Suggestions : ExploreSection
}

fun ExploreSection.toInt(): Int =
    when (this) {
        ExploreSection.Posts -> 0
        ExploreSection.Hashtags -> 1
        ExploreSection.Links -> 2
        ExploreSection.Suggestions -> 3
    }

fun Int.toExploreSection(): ExploreSection =
    when (this) {
        1 -> ExploreSection.Hashtags
        2 -> ExploreSection.Links
        3 -> ExploreSection.Suggestions
        else -> ExploreSection.Posts
    }

@Composable
fun ExploreSection.toReadableName(): String =
    when (this) {
        ExploreSection.Posts -> LocalStrings.current.accountSectionPosts
        ExploreSection.Hashtags -> LocalStrings.current.exploreSectionHashtags
        ExploreSection.Links -> LocalStrings.current.exploreSectionLinks
        ExploreSection.Suggestions -> LocalStrings.current.exploreSectionSuggestions
    }
