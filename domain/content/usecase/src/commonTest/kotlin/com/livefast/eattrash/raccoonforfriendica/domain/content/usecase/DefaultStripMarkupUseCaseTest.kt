package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase

import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.BBCodeConverter
import com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters.MarkdownConverter
import com.livefast.eattrash.raccoonforfriendica.domain.identity.data.MarkupMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultStripMarkupUseCaseTest {
    private val bbCodeConverter = mock<BBCodeConverter>()
    private val markdownConverter = mock<MarkdownConverter>()
    private val sut =
        DefaultStripMarkupUseCase(
            bbCodeConverter = bbCodeConverter,
            markdownConverter = markdownConverter,
        )

    @Test
    fun `given HTML input when invoked then result is as expected`() = runTest {
        val input = "<h1>Title</h1><p>Text</p>"

        val res = sut(text = input, mode = MarkupMode.HTML)

        assertEquals("Title\n\nText", res)
    }

    @Test
    fun `given BBCode input when invoked then result is as expected`() = runTest {
        every { bbCodeConverter.toHtml(any()) } returns "Title\nText"
        val input = "[h1]Title[/h1][p]Text[/p]"

        val res = sut(text = input, mode = MarkupMode.BBCode)

        assertEquals("Title\nText", res)
        verify { bbCodeConverter.toHtml(input) }
    }

    @Test
    fun `given Markdown input when invoked then result is as expected`() = runTest {
        every { markdownConverter.toHtml(any()) } returns "Title\nText"
        val input = "# Title\nText"

        val res = sut(text = input, mode = MarkupMode.Markdown)

        assertEquals("Title\nText", res)
        verify { markdownConverter.toHtml(input) }
    }
}
