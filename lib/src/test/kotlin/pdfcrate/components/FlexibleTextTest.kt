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
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
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
        val size = FlexibleText(TEST_TEXT, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verify { contentStream.setFont(any(), fontSize) }
        verifyShowText()
    }

    @Test
    fun getBlocksNoScaling() {
        mockContext()
        val fontSize = 20f
        val size = FlexibleText(TEST_TEXT, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.heightBlocks).isEqualTo(listOf(fontSize))
    }

    @Test
    fun writeFlexTextDownscaled() {
        mockContext()
        val fontSize = 2000f
        val size = FlexibleText(TEST_TEXT, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.height).isEqualTo(fontSize)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(size.height)) }
        verify { contentStream.newLineAtOffset(eq(0f), any()) }
        verifyShowText()
    }


    @Test
    fun getBlocksDownscaled() {
        mockContext()
        val fontSize = 500f
        val size = FlexibleText(TEST_TEXT, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.heightBlocks).isEqualTo(listOf(fontSize))
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
            style = Style.default(),
            renderContext = generateRenderContext(DEFAULT_SIZE),
        )
    }
}
