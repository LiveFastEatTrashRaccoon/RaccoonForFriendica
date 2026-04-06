package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class FriendicaInstance(val lang: String = "", val mau: Int = 0, val value: String)

/**
 * List of predefined Friendica instances in the drop-down menu.
 *
 * Source for MAU: https://fedidb.com/servers
 */
val DefaultFriendicaInstances =
    buildList {
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 559,
                value = "friendica.world",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 270,
                value = "friendica.eskimo.com",
            )
        this +=
            FriendicaInstance(
                lang = "🇮🇹",
                mau = 247,
                value = "poliverso.org",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 158,
                value = "loma.ml",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 117,
                value = "friendica.opensocial.space",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 89,
                value = "anonsys.net",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 54,
                value = "social.trom.tf",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 44,
                value = "joinfriendica.de",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 41,
                value = "inne.city",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 12,
                value = "friendica.me",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 10,
                value = "friendica.myportal.social",
            )
    }.sortedByDescending { it.mau }
