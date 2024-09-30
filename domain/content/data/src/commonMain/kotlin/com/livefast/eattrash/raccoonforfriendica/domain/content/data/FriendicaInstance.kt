package com.livefast.eattrash.raccoonforfriendica.domain.content.data

data class FriendicaInstance(
    val lang: String = "",
    val mau: Int = 0,
    val value: String,
)

val DefaultFriendicaInstances =
    buildList {
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 23,
                value = "friendica.me",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 194,
                value = "libranet.de",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 157,
                value = "loma.ml",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 89,
                value = "nerdica.net",
            )
        this +=
            FriendicaInstance(
                lang = "🇩🇪",
                mau = 40,
                value = "opensocial.at",
            )
        this +=
            FriendicaInstance(
                lang = "🇮🇹",
                mau = 184,
                value = "poliverso.org",
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
                mau = 83,
                value = "squeet.me",
            )
        this +=
            FriendicaInstance(
                lang = "🇬🇧",
                mau = 160,
                value = "venera.social",
            )
    }.sortedByDescending { it.mau }
