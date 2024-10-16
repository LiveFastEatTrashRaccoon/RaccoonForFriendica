package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

internal object ComposerRegexes {
    val USER_MENTION =
        Regex("@(?<handlePrefix>[a-zA-Z0-9-_.]+?(@[a-zA-Z0-9_.]+)?)(?=\\b)")
    val SHARE =
        Regex("\\[share](?<url>.*?)\\[share]")
    val HASHTAG =
        Regex("#(?<tag>.*?)(?=\\b)")
}
