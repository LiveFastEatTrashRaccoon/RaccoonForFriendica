package com.livefast.eattrash.raccoonforfriendica.core.appearance.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.LocalStrings

sealed interface CommentBarTheme {
    data object Red : CommentBarTheme

    data object Green : CommentBarTheme

    data object Blue : CommentBarTheme

    data object Rainbow : CommentBarTheme
}

fun Int?.toCommentBarTheme(): CommentBarTheme =
    when (this) {
        3 -> CommentBarTheme.Red
        2 -> CommentBarTheme.Green
        1 -> CommentBarTheme.Blue
        else -> CommentBarTheme.Rainbow
    }

fun CommentBarTheme.toInt(): Int =
    when (this) {
        CommentBarTheme.Red -> 3
        CommentBarTheme.Green -> 2
        CommentBarTheme.Blue -> 1
        CommentBarTheme.Rainbow -> 0
    }

@Composable
fun CommentBarTheme?.toReadableName() =
    when (this) {
        CommentBarTheme.Red -> LocalStrings.current.commentBarThemeRed
        CommentBarTheme.Green -> LocalStrings.current.commentBarThemeGreen
        CommentBarTheme.Blue -> LocalStrings.current.commentBarThemeBlue
        else -> LocalStrings.current.commentBarThemeRainbow
    }

fun CommentBarTheme?.toEmoji() =
    when (this) {
        CommentBarTheme.Red -> "\uD83E\uDD90"
        CommentBarTheme.Green -> "\uD83E\uDD8E"
        CommentBarTheme.Blue -> "\uD83D\uDC33"
        else -> "\uD83D\uDC0D"
    }
