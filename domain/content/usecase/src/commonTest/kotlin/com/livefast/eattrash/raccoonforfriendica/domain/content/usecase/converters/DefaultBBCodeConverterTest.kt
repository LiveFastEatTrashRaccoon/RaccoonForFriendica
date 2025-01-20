package com.livefast.eattrash.raccoonforfriendica.domain.content.usecase.converters

import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultBBCodeConverterTest {
    private val sut = DefaultBBCodeConverter()

    // region toHtml
    @Test
    fun `given strikethrough when toHtml then result is as expected`() {
        val input = "[s]text[/s]"
        val expected = "<s>text</s>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given italic when toHtml then result is as expected`() {
        val input = "[i]text[/i]"
        val expected = "<i>text</i>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given bold when toHtml then result is as expected`() {
        val input = "[b]text[/b]"
        val expected = "<b>text</b>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given monospace when toHtml then result is as expected`() {
        val input = "[code]text[/code]"
        val expected = "<code>text</code>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given anchor when toHtml then result is as expected`() {
        val input = "[url=url]anchor[/url]"
        val expected = "<a href=\"url\">anchor</a>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given share when toHtml then result is as expected`() {
        val input = "[share]url[/share]"
        val expected = "<a href=\"url\">url</a>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h1 when toHtml then result is as expected`() {
        val input = "[h1]text[/h1]"
        val expected = "<h1>text</h1>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h2 when toHtml then result is as expected`() {
        val input = "[h2]text[/h2]"
        val expected = "<h2>text</h2>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h3 when toHtml then result is as expected`() {
        val input = "[h3]text[/h3]"
        val expected = "<h3>text</h3>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h4 when toHtml then result is as expected`() {
        val input = "[h4]text[/h4]"
        val expected = "<h4>text</h4>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h5 when toHtml then result is as expected`() {
        val input = "[h5]text[/h5]"
        val expected = "<h5>text</h5>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given underlined when toHtml then result is as expected`() {
        val input = "[u]text[/u]"
        val expected = "<u>text</u>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given unordered list when toHtml then result is as expected`() {
        val input = "[ul][li]item[/li][/ul]"
        val expected = "<ul><li>item</li></ul>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given ordered list when toHtml then result is as expected`() {
        val input = "[ol][li]item[/li][/ol]"
        val expected = "<ol><li>item</li></ol>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given quote when toHtml then result is as expected`() {
        val input = "[quote]text[/quote]"
        val expected = "<blockquote>text</blockquote>"

        val res = sut.toHtml(input)

        assertEquals(expected, res)
    }
    // endregion

    // region fromHtml
    @Test
    fun `given strikethrough when fromHtml then result is as expected`() {
        val input = "<s>text</s>"
        val expected = "[s]text[/s]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given italic when fromHtml then result is as expected`() {
        val input = "<i>text</i>"
        val expected = "[i]text[/i]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given bold when fromHtml then result is as expected`() {
        val input = "<b>text</b>"
        val expected = "[b]text[/b]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given monospace when fromHtml then result is as expected`() {
        val input = "<code>text</code>"
        val expected = "[code]text[/code]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given anchor when fromHtml then result is as expected`() {
        val input = "<a href=\"url\">anchor</a>"
        val expected = "[url=url]anchor[/url]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h1 when fromHtml then result is as expected`() {
        val input = "<h1>text</h1>"
        val expected = "[h1]text[/h1]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h2 when fromHtml then result is as expected`() {
        val input = "<h2>text</h2>"
        val expected = "[h2]text[/h2]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h3 when fromHtml then result is as expected`() {
        val input = "<h3>text</h3>"
        val expected = "[h3]text[/h3]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h4 when fromHtml then result is as expected`() {
        val input = "<h4>text</h4>"
        val expected = "[h4]text[/h4]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given h5 when fromHtml then result is as expected`() {
        val input = "<h5>text</h5>"
        val expected = "[h5]text[/h5]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given underlined when fromHtml then result is as expected`() {
        val input = "<u>text</u>"
        val expected = "[u]text[/u]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given unordered list when fromHtml then result is as expected`() {
        val input = "<ul><li>item</li></ul>"
        val expected = "[ul][li]item[/li][/ul]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given ordered list when fromHtml then result is as expected`() {
        val input = "<ol><li>item</li></ol>"
        val expected = "[ol][li]item[/li][/ol]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given block quote when fromHtml then result is as expected`() {
        val input = "<blockquote>text</blockquote>"
        val expected = "[quote]text[/quote]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }

    @Test
    fun `given inline quote when fromHtml then result is as expected`() {
        val input = "<q>text</q>"
        val expected = "[quote]text[/quote]"

        val res = sut.fromHtml(input)

        assertEquals(expected, res)
    }
    // endregion
}
