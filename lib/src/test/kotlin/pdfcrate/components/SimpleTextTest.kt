package pdfcrate.components

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.render.ContentStreamWrapper
import pdfcrate.render.PageStream

private const val DEFAULT_SIZE = 500f
private const val TEST_TEXT = "TESTING TEXT"

private const val DEFAULT_FONT_SIZE = 13f

@ExtendWith(MockKExtension::class)
class SimpleTextTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    @Test
    fun writeSingleLine() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val size = SimpleText(TEST_TEXT, TextStyle(fontSize = fontSize)).render(context)
        Assertions.assertThat(size.width).isLessThan(DEFAULT_SIZE)
        Assertions.assertThat(size.height).isEqualTo(fontSize)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verify { contentStream.setFont(any(), fontSize) }
        verify { contentStream.showText(eq(TEST_TEXT)) }
        verifyBasicCommands()
    }


    @Test
    fun writeMultipleLines() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val text = "$TEST_TEXT\n$TEST_TEXT\n$TEST_TEXT"
        val size = SimpleText(text, TextStyle(fontSize = fontSize)).render(context)
        Assertions.assertThat(size.width).isLessThan(DEFAULT_SIZE)
        Assertions.assertThat(size.height).isEqualTo(fontSize * 3)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify { contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE - fontSize)) }
        verify(exactly = 3) { contentStream.newLine() }
        verify { contentStream.setFont(any(), fontSize) }
        verify(exactly = 3) { contentStream.showText(eq(TEST_TEXT)) }
        verifyBasicCommands()
    }

    @Test
    fun writeMultipleLinesPageBreak() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        var text = "$TEST_TEXT\n"
        for (i in 0..50) {
            text += "$TEST_TEXT\n"
        }
        val size = SimpleText(text, TextStyle(fontSize = fontSize)).render(context)
        Assertions.assertThat(size.width).isLessThan(DEFAULT_SIZE)
        Assertions.assertThat(size.height).isGreaterThan(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify(exactly = 2) { contentStream.newLineAtOffset(any(), any()) }
        verifyBasicCommands(times = 2)
    }

    private fun verifyBasicCommands(fontSize: Float = DEFAULT_FONT_SIZE, times: Int = 1) {
        verify(exactly = times) { contentStream.setFont(any(), fontSize) }
        verify(exactly = times) { contentStream.beginText() }
        verify(exactly = times) { contentStream.endText() }
    }


    private fun mockContext() {
        val offset = slot<Float>()
        val height = slot<Float>()
        every { pageStream.contentStreamFor(capture(offset), capture(height)) } answers {
            if (offset.captured + height.captured > DEFAULT_SIZE) {
                ContentStreamWrapper(
                    stream = contentStream,
                    page = 1,
                    realOffset = DEFAULT_SIZE,
                    virtualOffset = offset.captured,
                )
            } else {
                ContentStreamWrapper(
                    stream = contentStream,
                    page = 0,
                    realOffset = DEFAULT_SIZE - offset.captured,
                    virtualOffset = offset.captured,
                )
            }

        }
        context = ComponentContext(
            pages = pageStream,
            x = 0f,
            maxX = DEFAULT_SIZE,
            y = 0f,
            style = Style.DEFAULT_STYLE
        )
    }
}
