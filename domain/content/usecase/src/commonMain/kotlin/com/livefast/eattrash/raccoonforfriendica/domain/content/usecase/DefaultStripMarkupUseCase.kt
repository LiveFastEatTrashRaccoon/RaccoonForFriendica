package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.MarkdownConverter
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode

internal class DefaultStripMarkupUseCase(
    private val bbCodeConverter: BBCodeConverter,
    private val markdownConverter: MarkdownConverter,
) : StripMarkupUseCase {
    override fun invoke(text: String, mode: MarkupMode): String {
        val rendered =
            when (mode) {
                MarkupMode.BBCode -> bbCodeConverter.toHtml(text)
                MarkupMode.Markdown -> markdownConverter.toHtml(text)
                else -> text
            }
        val html = rendered.parseHtml(Color.Unspecified, Color.Unspecified)
        return html.toString()
    }
}
