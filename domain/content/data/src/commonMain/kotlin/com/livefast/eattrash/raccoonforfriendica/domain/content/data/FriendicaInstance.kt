package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class FriendicaInstance(val lang: String = "", val node: String)

/**
 * List of predefined Friendica instances in the drop-down menu.
 */
val DefaultFriendicaInstances =
    buildList {
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                node = "social.trom.tf",
            )
        this +=
            FriendicaInstance(
                lang = "🇮🇹",
                node = "poliverso.org",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                node = "friendica.world",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                node = "nerdica.net",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                node = "opensocial.at",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                node = "inne.city",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                node = "friendica.xyz",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                node = "friendica.me",
            )
    }
