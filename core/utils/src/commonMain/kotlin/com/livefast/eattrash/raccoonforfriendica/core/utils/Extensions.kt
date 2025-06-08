package com.livefast.eattrash.raccoonforfriendica.core.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

fun String?.ellipsize(length: Int = 100, ellipsis: String = "…"): String {
    check(!isNullOrEmpty()) { return "" }
    check(this.length >= length) { return this }
    return take(length - 1) + ellipsis
}

val String?.nodeName: String?
    get() =
        orEmpty()
            .let {
                if (it.contains('@')) {
                    it.substringAfter('@')
                } else {
                    ""
                }
            }.takeIf { it.isNotEmpty() }

val String?.detailName: String?
    get() =
        orEmpty()
            .let {
                if (it.contains('@')) {
                    it.substringBefore('@')
                } else {
                    ""
                }
            }.takeIf { it.isNotEmpty() }

fun Int.isNearTheEnd(list: List<*>): Boolean = this >= list.lastIndex - 5 || list.size <= 5

fun Regex.substituteAllOccurrences(original: String, onMatchResult: StringBuilder.(MatchResult) -> Unit): String =
    buildString {
        val matches = findAll(original).toList()
        var index = 0
        for (match in matches) {
            val range = match.range
            append(original.subSequence(index, range.first))
            onMatchResult(match)
            index = range.last + 1
        }
        if (index < original.length) {
            append(original.subSequence(index, original.length))
        }
    }

fun Regex.substituteAllOccurrences(
    original: AnnotatedString,
    onMatchResult: AnnotatedString.Builder.(MatchResult) -> Unit,
): AnnotatedString = buildAnnotatedString {
    val matches = findAll(original).toList()
    var index = 0
    for (match in matches) {
        val range = match.range
        append(original.subSequence(index, range.first))
        onMatchResult(match)
        index = range.last + 1
    }
    if (index < original.length) {
        append(original.subSequence(index, original.length))
    }
}
