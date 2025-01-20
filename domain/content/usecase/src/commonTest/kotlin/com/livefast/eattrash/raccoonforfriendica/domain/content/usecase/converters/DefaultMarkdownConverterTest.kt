package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters

import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultMarkdownConverterTest {
    private val sut = DefaultMarkdownConverter()

    @Test
    fun `given strikethrough when toHtml then result is as expected`() {
        val input = "~~text~~"
        val expected = "<s>text</s>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given italic when toHtml then result is as expected`() {
        val input = "_text_"
        val expected = "<i>text</i>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given bold when toHtml then result is as expected`() {
        val input = "**text**"
        val expected = "<b>text</b>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given monospace when toHtml then result is as expected`() {
        val input = "`text`"
        val expected = "<code>text</code>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given anchor when toHtml then result is as expected`() {
        val input = "[anchor](url)"
        val expected = "<a href=\"url\">anchor</a>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h1 when toHtml then result is as expected`() {
        val input = "# text"
        val expected = "<h1>text</h1>\n"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h2 when toHtml then result is as expected`() {
        val input = "## text"
        val expected = "<h2>text</h2>\n"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h3 when toHtml then result is as expected`() {
        val input = "### text"
        val expected = "<h3>text</h3>\n"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h4 when toHtml then result is as expected`() {
        val input = "#### text"
        val expected = "<h4>text</h4>\n"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h5 when toHtml then result is as expected`() {
        val input = "##### text"
        val expected = "<h5>text</h5>\n"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given share when toHtml then result is as expected`() {
        val input = "[share]url[/share]"
        val expected = "url"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `when fromHtml then result is as expected`() {
        val input = "lorem ipsum"
        val expected = "lorem ipsum"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }
}
