package pdfcrate.components

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.render.ContentStreamWrapper
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext

private const val DEFAULT_SIZE = 500f
private const val TEST_TEXT = "TESTING TEXT"

private const val DEFAULT_FONT_SIZE = 13f

@ExtendWith(MockKExtension::class)
class TextTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    @Test
    fun writeSingleLine() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val size = Text(TEST_TEXT, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize)
        verify {
            pageStream.contentStreamFor(eq(0f), eq(fontSize))
            contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE))
            contentStream.newLine()
            contentStream.setFont(any(), fontSize)
            contentStream.showText(eq(TEST_TEXT))
        }
        verifyBasicCommands()
    }


    @Test
    fun writeMultipleLines() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val text = "$TEST_TEXT\n$TEST_TEXT\n$TEST_TEXT"
        val size = Text(text, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize * 3)

        verify {
            pageStream.contentStreamFor(eq(0f), eq(fontSize))
            contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE))
            contentStream.setFont(any(), fontSize)
        }
        verify(exactly = 3) {
            contentStream.showText(eq(TEST_TEXT))
            contentStream.newLine()
        }
        verifyBasicCommands()
    }

    @Test
    fun writeMultipleLinesRightAligned() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val lines = List(3) { TEST_TEXT }
        val size = Text.rightAlign(lines, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize * 3)

        verify {
            pageStream.contentStreamFor(eq(0f), eq(fontSize))
            contentStream.newLineAtOffset(eq(0f), eq(-fontSize))
            contentStream.newLineAtOffset(neq(0f), eq(-fontSize))
            contentStream.setFont(any(), fontSize)
        }
        verify(exactly = 2) {
            contentStream.newLineAtOffset(eq(0f), eq(-fontSize))
        }
        verify(exactly = 3) {
            contentStream.showText(eq(TEST_TEXT))
        }
        verifyBasicCommands()
    }


    @Test
    fun writeSingleJustified() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val size = Text.justify(TEST_TEXT, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize)

        verify {
            pageStream.contentStreamFor(eq(0f), eq(fontSize))
            contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE))
            contentStream.setFont(any(), fontSize)
            contentStream.showText(eq(TEST_TEXT))
            contentStream.newLine()
        }
        verifyBasicCommands()
    }

    @Test
    fun writeMultipleJustified() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val lines = List(3) { TEST_TEXT }
        val size = Text.justify(lines, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(fontSize * 3)

        verify {
            pageStream.contentStreamFor(eq(0f), eq(fontSize))
            contentStream.newLineAtOffset(eq(0f), eq(DEFAULT_SIZE))
            contentStream.setFont(any(), fontSize)
        }
        verify(exactly = 3) {
            contentStream.showText(eq(TEST_TEXT))
            contentStream.newLine()
        }
        verify(exactly = 2) {
            contentStream.setWordSpacing(any())
        }
        verifyBasicCommands()
    }

    @Test
    fun writeMultipleLinesPageBreak() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val lines = List(50) { TEST_TEXT }
        val size = Text(lines, TextStyle(fontSize = fontSize)).render(context)
        assertThat(size.width).isLessThan(DEFAULT_SIZE)
        assertThat(size.height).isGreaterThan(DEFAULT_SIZE)
        verify { pageStream.contentStreamFor(eq(0f), eq(fontSize)) }
        verify(exactly = 2) { contentStream.newLineAtOffset(any(), any()) }
        verifyBasicCommands(times = 2)
    }

    @Test
    fun sizeBlocksLeft() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val text = "$TEST_TEXT\n$TEST_TEXT\n"
        val size = Text(text, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.heightBlocks).hasSize(3)
        assertThat(size.heightBlocks).allMatch { it == fontSize }
    }

    @Test
    fun sizeBlocksJustified() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val text = "$TEST_TEXT\n$TEST_TEXT\n"
        val size = Text.justify(text, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.heightBlocks).hasSize(3)
        assertThat(size.heightBlocks).allMatch { it == fontSize }
    }

    @Test
    fun sizeBlocksRightAlign() {
        mockContext()
        val fontSize = DEFAULT_FONT_SIZE
        val text = "$TEST_TEXT\n$TEST_TEXT\n"
        val size = Text.rightAlign(text, TextStyle(fontSize = fontSize)).getBlocks(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.heightBlocks).hasSize(3)
        assertThat(size.heightBlocks).allMatch { it == fontSize }
    }

    private fun verifyBasicCommands(fontSize: Float = DEFAULT_FONT_SIZE, times: Int = 1) {
        verify(exactly = times) {
            contentStream.beginText()
            contentStream.setFont(any(), fontSize)
            contentStream.endText()
        }
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
            style = Style.default(),
            renderContext = generateRenderContext(DEFAULT_SIZE),
        )
    }
}
