package pdfcrate.components

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
import pdfcrate.testutil.mockPageStreamSimple
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

private const val DEFAULT_SIZE = 500f

@ExtendWith(MockKExtension::class)
class PaddingTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    @MockK
    lateinit var component: SizedComponent

    private lateinit var context: ComponentContext

    @Test
    fun zeroPaddingBlocks() {
        mockContext()
        mockComponent()
        val padding = Padding(component, Edges.ZERO)
        val blocks = padding.getBlocks(context)
        assertThat(blocks.width).isEqualTo(10f)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(0f, 20f, 0f))
    }

    @Test
    fun zeroPaddingRender() {
        mockContext()
        mockComponent()
        val padding = Padding(component, Edges.ZERO)
        val size = padding.render(context)
        assertThat(size).isEqualTo(Size(10f, 20f))
    }

    @Test
    fun singleSideBlocks() {
        mockContext()
        mockComponent()
        val padding = Padding(component, Edges(10f, 0f, 0f, 0f))
        val blocks = padding.getBlocks(context)
        assertThat(blocks.width).isEqualTo(10f)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(10f, 20f, 0f))
    }

    @Test
    fun singleSideRender() {
        mockContext()
        mockComponent()
        val padding = Padding(component, Edges(10f, 0f, 0f, 0f))
        val size = padding.render(context)
        assertThat(size).isEqualTo(Size(10f, 30f))
    }

    @Test
    fun allSideBlocks() {
        mockContext()
        mockComponent()
        val padding = Padding(component, Edges(10f, 20f, 30f, 40f))
        val blocks = padding.getBlocks(context)
        assertThat(blocks.width).isEqualTo(70f)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(10f, 20f, 30f))
    }

    @Test
    fun allSideRender() {
        mockContext()
        mockComponent()
        val padding = Padding(component, Edges(10f, 20f, 30f, 40f))
        val size = padding.render(context)
        assertThat(size).isEqualTo(Size(70f, 60f))
    }

    private fun mockComponent(width: Float = 10f, height: Float = 20f) {
        every { component.render(any()) } returns Size(width, height)
        every { component.getBlocks(any()) } returns SizeBlocks.single(width, height)
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
