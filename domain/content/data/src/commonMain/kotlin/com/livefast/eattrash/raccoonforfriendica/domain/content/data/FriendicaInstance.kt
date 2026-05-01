package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class FriendicaInstance(val lang: String = "", val value: String)

/**
 * List of predefined Friendica instances in the drop-down menu.
 */
val DefaultFriendicaInstances =
    buildList {
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                value = "social.trom.tf",
            )
        this +=
            FriendicaInstance(
                lang = "🇮🇹",
                value = "poliverso.org",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                value = "friendica.world",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                value = "nerdica.net",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                value = "opensocial.at",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                value = "inne.city",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                value = "friendica.xyz",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                value = "friendica.me",
            )
    }
