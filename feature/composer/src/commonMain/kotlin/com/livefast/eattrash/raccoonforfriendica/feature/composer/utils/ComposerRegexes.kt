package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

internal object ComposerRegexes {
    val USER_MENTION =
        Regex("@(?<handlePrefix>[a-zA-Z0-9-_.]+?(@[a-zA-Z0-9_.]+)?)(?=\\b)")
    val BBCODE_SHARE =
        Regex("\\[share](?<url>.*?)\\[/share]")
    val HASHTAG =
        Regex("#(?<hashtag>.+?)(?=\\b)")
    val BBCODE_URL =
        Regex("\\[url=(?<url>.*?)](?<anchor>.*?)\\[/url]")
    val HTML_URL =
        Regex("<a \"href\"=(?<url>.*?)>(?<anchor>.*?)</a>")
}
