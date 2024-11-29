package com.livefast.eattrash.raccoonforfriendica.feature.composer.utils

import androidx.compose.ui.graphics.Color
import com.livefast.eattrash.raccoonforfriendica.core.htmlparse.parseHtml
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode

internal class DefaultStripMarkupUseCase(
    private val prepareForPreview: PrepareForPreviewUseCase,
) : StripMarkupUseCase {
    override fun invoke(
        text: String,
        mode: MarkupMode,
    ): String {
        val rendered = prepareForPreview(text = text, mode = mode)
        val html = rendered.parseHtml(Color.Unspecified, Color.Unspecified)
        return html.toString()
    }
}
