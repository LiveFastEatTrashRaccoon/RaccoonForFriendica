package com.livefast.eattrash.raccoonforfriendica.core.commonui.content

import kotlin.test.Test
import kotlin.test.assertEquals

class ContentBodyTest {

    @Test
    fun `given an inline emoji, when splitting text and images, then the emoji stays in the text chunk`() {
        val html = """Text before <img src="emoji.png" alt=":emoji:" /> text after"""

        val chunks = html.splitTextAndImages()

        assertEquals(1, chunks.size)
        assertEquals("Text before <img src=\"emoji.png\" alt=\":emoji:\" /> text after", chunks[0])
    }

    @Test
    fun `given a real image, when splitting text and images, then the image is in its own chunk`() {
        val html = """Text before <img src="image.png" alt="An image" /> text after"""

        val chunks = html.splitTextAndImages()

        assertEquals(3, chunks.size)
        assertEquals("Text before", chunks[0].trim())
        assertEquals("<img src=\"image.png\" alt=\"An image\" />", chunks[1])
        assertEquals("text after", chunks[2].trim())
    }

    @Test
    fun `given hashtag and image, when splitting text and images, then it returns both chunks without truncation`() {
        val html = """<p>Check this #hashtag</p><img src="image.png" alt="image" />"""

        val chunks = html.splitTextAndImages()

        assertEquals(2, chunks.size)
        assertEquals("<p>Check this #hashtag</p>", chunks[0])
        assertEquals("<img src=\"image.png\" alt=\"image\" />", chunks[1])
    }

    @Test
    fun `given a multiline image tag, when splitting text and images, then it correctly identifies the image chunk`() {
        val html = """Text before <img
             src="image.png"
             alt="image" /> text after
        """.trimMargin()

        val chunks = html.splitTextAndImages()

        assertEquals(3, chunks.size)
        assertEquals("Text before", chunks[0].trim())
        assertEquals(
            """<img
             src="image.png"
             alt="image" />
            """.trimMargin(),
            chunks[1],
        )
        assertEquals("text after", chunks[2].trim())
    }

    @Test
    fun `given hashtag and inline image, when splitting text and images, then link stays in text chunk`() {
        val html = """<p>#<a href="https://poliverso.org/search?tag=Rainews">Rainews</a> riesca a pubblicare...</p>
               <p>E non è l'unico...<br>
               <a href="https://poliverso.org/photos/1"><img src="https://poliverso.org/photo/1.png" alt="" /></a>
               </p>
        """.trimMargin()

        val chunks = html.splitTextAndImages()

        assertEquals(3, chunks.size)
        assertEquals(
            """<p>#<a href="https://poliverso.org/search?tag=Rainews">Rainews</a> riesca a pubblicare...</p>
               <p>E non è l'unico...<br>
            """.trimMargin(),
            chunks[0].trim(),
        )
        assertEquals(
            "<a href=\"https://poliverso.org/photos/1\"><img src=\"https://poliverso.org/photo/1.png\" alt=\"\" /></a>",
            chunks[1],
        )
        assertEquals("</p>", chunks[2])
    }

    @Test
    fun `given inline image in a link, when splitting text and images, then the wrapped image is in its own chunk`() {
        val html =
            """Text before <a href="https://example.com/photo"><img src="photo.jpg" alt="photo" /></a> text after"""

        val chunks = html.splitTextAndImages()

        assertEquals(3, chunks.size)
        assertEquals("Text before", chunks[0].trim())
        assertEquals(
            """<a href="https://example.com/photo"><img src="photo.jpg" alt="photo" /></a>""",
            chunks[1],
        )
        assertEquals("text after", chunks[2].trim())
    }
}
