package com.livefast.eattrash.raccoonforfriendica.core.api.dto

import kotlinx.serialization.SerialName

enum class SuggestionSource {
    @Deprecated("Replaced by FEATURED")
    @SerialName("staff")
    STAFF,

    @Deprecated("Replaced by one of SIMILAR_TO_RECENTLY_FOLLOWED, FRIENDS_OF_FRIENDS")
    @SerialName("past_interactions")
    PAST_INTERACTIONS,

    @Deprecated("Replaced by one of MOST_FOLLOWED, MOST_INTERACTIONS")
    @SerialName("global")
    GLOBAL,

    @SerialName("similar_to_recently_followed")
    SIMILAR_TO_RECENTLY_FOLLOWED,

    @SerialName("friends_of_friends")
    FRIENDS_OF_FRIENDS,

    @SerialName("most_followed")
    MOST_FOLLOWED,

    @SerialName("most_interactions")
    MOST_INTERACTIONS,

    @SerialName("featured")
    FEATURED,
}
