package pdfcrate.components

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
import pdfcrate.testutil.mockPageStreamSimple
import pdfcrate.testutil.verifyComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

private const val DEFAULT_SIZE = 500f

@ExtendWith(MockKExtension::class)

class CenterTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    @Test
    fun centerSmallElementGetSize() {
        mockContext()
        val component = mockComponent()
        val blocks = Center(component).getBlocks(context)
        assertThat(blocks.width).isEqualTo(DEFAULT_SIZE)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(10f))
    }

    @Test
    fun centerSmallElementRender() {
        mockContext()
        val component = mockComponent()
        val size = Center(component).render(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(10f)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 245f, maxX = 255f, y = 0f)
    }

    @Test
    fun centerFullWidthGetSize() {
        mockContext()
        val component = mockComponent(width = DEFAULT_SIZE)
        val blocks = Center(component).getBlocks(context)
        assertThat(blocks.width).isEqualTo(DEFAULT_SIZE)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(10f))
    }

    @Test
    fun centerFullWidthRender() {
        mockContext()
        val component = mockComponent(width = DEFAULT_SIZE)
        val size = Center(component).render(context)
        assertThat(size.width).isEqualTo(DEFAULT_SIZE)
        assertThat(size.height).isEqualTo(10f)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }


    @Test
    fun centerTooWideGetSize() {
        mockContext()
        val component = mockComponent(width = 1000f)
        val blocks = Center(component).getBlocks(context)
        assertThat(blocks.width).isEqualTo(1000f)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(10f))
    }

    @Test
    fun centerTooWideRender() {
        mockContext()
        val component = mockComponent(width = 1000f)
        val size = Center(component).render(context)
        assertThat(size.width).isEqualTo(1000f)
        assertThat(size.height).isEqualTo(10f)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = 1000f, y = 0f)
    }

    private fun mockComponent(width: Float = 10f, height: Float = 10f): SizedComponent {
        val component = mockk<SizedComponent>()
        every { component.getBlocks(any()) } returns SizeBlocks.single(width, height)
        every { component.render(any()) } returns Size(width, height)
        return component
    }

    private fun mockContext() {
        mockPageStreamSimple(pageStream, contentStream, DEFAULT_SIZE)
        context = ComponentContext(
            pages = pageStream,
            x = 0f,
            maxX = DEFAULT_SIZE,
            y = 0f,
            style = Style.DEFAULT_STYLE,
            renderContext = generateRenderContext(DEFAULT_SIZE),
        )
    }
}
