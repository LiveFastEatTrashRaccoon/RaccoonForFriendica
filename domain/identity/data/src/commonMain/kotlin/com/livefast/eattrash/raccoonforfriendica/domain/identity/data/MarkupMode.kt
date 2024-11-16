package com.livefast.eattrash.raccoonforfriendica.domain.identity.data

import androidx.compose.runtime.Composable
import com.livefast.eattrash.raccoonforfriendica.core.l10n.messages.LocalStrings

sealed interface MarkupMode {
    val supportsRichEditing: Boolean

    data object PlainText : MarkupMode {
        override val supportsRichEditing: Boolean get() = false
    }

    data object HTML : MarkupMode {
        override val supportsRichEditing: Boolean get() = true
    }

    data object BBCode : MarkupMode {
        override val supportsRichEditing: Boolean get() = true
    }

    data object Markdown : MarkupMode {
        override val supportsRichEditing: Boolean get() = true
    }
}

@Composable
fun MarkupMode.toReadableName() =
    when (this) {
        MarkupMode.BBCode -> LocalStrings.current.markupModeBBCode
        MarkupMode.HTML -> LocalStrings.current.markupModeHTML
        MarkupMode.Markdown -> LocalStrings.current.markupModeMarkdown
        MarkupMode.PlainText -> LocalStrings.current.markupModePlainText
    }

fun Int.toMarkupMode(): MarkupMode =
    when (this) {
        1 -> MarkupMode.HTML
        2 -> MarkupMode.BBCode
        3 -> MarkupMode.Markdown
        else -> MarkupMode.PlainText
    }

fun MarkupMode.toInt() =
    when (this) {
        MarkupMode.HTML -> 1
        MarkupMode.BBCode -> 2
        MarkupMode.Markdown -> 3
        MarkupMode.PlainText -> 0
    }
