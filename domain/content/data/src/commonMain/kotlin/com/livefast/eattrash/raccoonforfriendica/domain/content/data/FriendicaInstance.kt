package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class FriendicaInstance(
    val lang: String = "",
    val mau: Int = 0,
    val value: String,
)

/**
 * List of predefined Friendica instances in the drop-down menu.
 *
 * Source for MAU: https://the-federation.info/platform/11
 */
val DefaultFriendicaInstances =
    buildList {
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 21,
                value = "friendica.me",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 11,
                value = "friendica.myportal.social",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 1176,
                value = "friendica.world",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 154,
                value = "libranet.de",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 206,
                value = "anonsys.net",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 165,
                value = "loma.ml",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 101,
                value = "friendica.opensocial.space",
            )
        this +=
            FriendicaInstance(
                lang = "🇮🇹",
                mau = 270,
                value = "poliverso.org",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 78,
                value = "social.trom.tf",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 120,
                value = "venera.social",
            )
    }.sortedByDescending { it.mau }
