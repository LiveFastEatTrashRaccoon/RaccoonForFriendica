package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

internal fun String.replaceNewlines(): String =
    replace(Regex("\n\n"), "<br /><br />")
        .replace("\n", " ")
