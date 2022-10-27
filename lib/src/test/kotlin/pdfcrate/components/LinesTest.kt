package pdfcrate.components

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.LineStyle
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
import pdfcrate.testutil.mockPageStreamSimple
import pdfcrate.util.Point
import pdfcrate.util.Size
import java.awt.Color

private const val DEFAULT_SIZE = 500f

@ExtendWith(MockKExtension::class)
class LinesTest {
    private lateinit var context: ComponentContext

    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    @Test
    fun testVerticalLine() {
        mockContext()
        val size = Lines(listOf(Point(100f, 100f), Point(100f, 200f))).render(context)
        assertThat(size).isEqualTo(Size(100f, 200f))
    }

    @Test
    fun testHorizontalLine() {
        mockContext()
        val size = Lines(listOf(Point(200f, 100f), Point(100f, 100f))).render(context)
        assertThat(size).isEqualTo(Size(200f, 100f))
    }

    @Test
    fun testMultipleLines() {
        mockContext()
        val size =
            Lines(listOf(Point(200f, 120f), Point(100f, 100f), Point(50f, 50f), Point(300f, 50f))).render(context)
        assertThat(size).isEqualTo(Size(300f, 120f))
    }

    @Test
    fun testSizeBlocks() {
        mockContext()
        val size =
            Lines(listOf(Point(200f, 120f), Point(100f, 100f), Point(50f, 50f), Point(300f, 50f))).getBlocks(context)
        assertThat(size.width).isEqualTo(300f)
        assertThat(size.heightBlocks).hasSize(1).allMatch { it == 120f }
    }

    @Test
    fun testBuilder() {
        mockContext()
        val lines =
            Lines.builder().points(listOf(Point(50f, 100f), Point(0f, 100f)))
                .lineStyle(
                    LineStyle(
                        color = Color.BLUE,
                        width = 10f,
                    )
                ).build()
        val size = lines.render(context)
        assertThat(size).isEqualTo(Size(50f, 100f))
        verify { contentStream.setLineWidth(10f) }
        verify { contentStream.setStrokingColor(Color.BLUE) }
    }


    private fun mockContext() {
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
