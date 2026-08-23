package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import kotlin.test.Test
import kotlin.test.assertEquals

class ContentBodyTest {

    @Test
    fun `given an inline emoji, when splitting text and images, then the emoji stays in the text chunk`() {
        val html = "Text before <img src=\"emoji.png\" alt=\":emoji:\" /> text after"

        val chunks = html.splitTextAndImages()

        assertEquals(1, chunks.size)
        assertEquals("Text before <img src=\"emoji.png\" alt=\":emoji:\" /> text after", chunks[0])
    }

    @Test
    fun `given a real image, when splitting text and images, then the image is in its own chunk`() {
        val html = "Text before <img src=\"image.png\" alt=\"An image\" /> text after"

        val chunks = html.splitTextAndImages()

        assertEquals(3, chunks.size)
        assertEquals("Text before", chunks[0].trim())
        assertEquals("<img src=\"image.png\" alt=\"An image\" />", chunks[1])
        assertEquals("text after", chunks[2].trim())
    }

    @Test
    fun `given hashtag and image, when splitting text and images, then it returns both chunks without truncation`() {
        val html = "<p>Check this #hashtag</p><img src=\"image.png\" alt=\"image\" />"

        val chunks = html.splitTextAndImages()

        assertEquals(2, chunks.size)
        assertEquals("<p>Check this #hashtag</p>", chunks[0])
        assertEquals("<img src=\"image.png\" alt=\"image\" />", chunks[1])
    }

    @Test
    fun `given a multiline image tag, when splitting text and images, then it correctly identifies the image chunk`() {
        val html = "Text before <img\n src=\"image.png\"\n alt=\"image\" /> text after"

        val chunks = html.splitTextAndImages()

        assertEquals(3, chunks.size)
        assertEquals("Text before", chunks[0].trim())
        assertEquals("<img\n src=\"image.png\"\n alt=\"image\" />", chunks[1])
        assertEquals("text after", chunks[2].trim())
    }
}
