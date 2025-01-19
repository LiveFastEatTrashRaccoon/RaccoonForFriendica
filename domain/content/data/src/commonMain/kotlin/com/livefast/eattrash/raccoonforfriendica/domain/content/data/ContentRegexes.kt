package com.livefast.eattrash.raccoonforfriendica.domain.content.data

object ContentRegexes {
    val USER_MENTION =
        Regex("@(?<handlePrefix>[a-zA-Z0-9-_.]+?(@[a-zA-Z0-9_.]+)?)(?=\\b)")
    val BBCODE_SHARE =
        Regex("\\[share](?<url>.*?)\\[/share]")
    val HASHTAG =
        Regex("#(?<hashtag>.+?)(?=\\b)")
    val BBCODE_URL =
        Regex("\\[url=(?<url>.*?)](?<anchor>.*?)\\[/url]")
}
