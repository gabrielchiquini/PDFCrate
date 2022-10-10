package pdfcrate.components

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.document.TextStyle
import pdfcrate.render.Alignment
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.mockPageStreamSimple

private const val DEFAULT_SIZE = 500f
private const val TEST_TEXT = "TESTING TEXT"

@ExtendWith(MockKExtension::class)
class FlexibleTextTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    @Test
    fun writeFlexTextNoScaling() {
        mockContext()
        val fontSize = 20f
        val size = FlexibleText(TEST_TEXT, Alignment.LEFT, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verify { contentStream.setFont(any(), fontSize) }
        verifyShowText()
    }

    @Test
    fun writeFlexTextDownscaled() {
        mockContext()
        val fontSize = 2000f
        val size = FlexibleText(TEST_TEXT, Alignment.LEFT, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.height).isLessThan(fontSize)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(size.height)) }
        verify { contentStream.newLineAtOffset(eq(0f), any()) }
        verifyShowText()
    }

    @Test
    fun writeFlexTextCentered() {
        mockContext()
        val fontSize = 20f
        val size = FlexibleText(TEST_TEXT, Alignment.CENTER, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.height).isEqualTo(fontSize)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(neq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verifyShowText()
    }

    @Test
    fun getTextSizeCenter() {
        mockContext()
        val fontSize = 20f
        val size = FlexibleText(TEST_TEXT, Alignment.CENTER, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.heightBlocks).hasSize(1).allMatch { it == fontSize }
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
    }

    @Test
    fun getTextSizeLeft() {
        mockContext()
        val fontSize = 20f
        val size = FlexibleText(TEST_TEXT, Alignment.LEFT, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.heightBlocks).hasSize(1).allMatch { it == fontSize }
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
    }

    private fun verifyShowText() {
        verify { contentStream.showText(eq(TEST_TEXT)) }
        verify { contentStream.beginText() }
        verify { contentStream.endText() }
    }


    private fun mockContext() {
        pageStream = mockk()
        contentStream = mockk(relaxed = true)
        mockPageStreamSimple(pageStream, contentStream, DEFAULT_SIZE)
        context = ComponentContext(
            pages = pageStream,
            x = 0f,
            maxX = DEFAULT_SIZE,
            y = 0f,
            style = Style.DEFAULT_STYLE
        )
    }
}
