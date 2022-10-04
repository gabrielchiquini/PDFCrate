package pdfcrate.render

import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.TextStyle

private const val DEFAULT_SIZE = 500f
private const val TEST_TEXT = "TESTING TEXT"

@ExtendWith(MockKExtension::class)
class ContentBuilderTest {
    lateinit var pageStream: PageStream

    lateinit var contentStream: PDPageContentStream

    lateinit var builder: ContentBuilder

    @Test
    fun writeFlexTextNoScaling() {
        init()
        val fontSize = 20f
        mockWrapper()
        val size = builder.writeFlexSizeText(TEST_TEXT, TextStyle(fontSize = fontSize), Alignment.LEFT)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verify { contentStream.setFont(any(), fontSize) }
        verifyShowText()
    }

    @Test
    fun writeFlexTextDownscaled() {
        init()
        val fontSize = 2000f
        mockWrapper()
        val size = builder.writeFlexSizeText(TEST_TEXT, TextStyle(fontSize = fontSize), Alignment.LEFT)
        assertThat(size.height).isLessThan(fontSize)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(size.height)) }
        verify { contentStream.newLineAtOffset(eq(0f), any()) }
        verifyShowText()
    }

    @Test
    fun writeFlexTextCentered() {
        init()
        mockWrapper()
        val fontSize = 20f
        val size = builder.writeFlexSizeText(TEST_TEXT, TextStyle(fontSize = fontSize), Alignment.CENTER)
        assertThat(size.height).isEqualTo(fontSize)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(neq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verifyShowText()
    }

    private fun verifyShowText() {
        verify { contentStream.showText(eq(TEST_TEXT)) }
        verify { contentStream.beginText() }
        verify { contentStream.endText() }
    }

    private fun mockWrapper() {
        every { pageStream.contentStreamFor(any(), any()) } returns ContentStreamWrapper(
            stream = contentStream,
            page = 0,
            realOffset = DEFAULT_SIZE,
            virtualOffset = 0f,
        )
    }

    private fun init() {
        pageStream = mockk()
        contentStream = mockk(relaxed = true)
        builder = ContentBuilder(pages = pageStream, x = 0f, maxX = DEFAULT_SIZE, startingY = 0f)
    }
}
