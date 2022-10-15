package pdfcrate.components

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
import pdfcrate.testutil.mockComponent
import pdfcrate.testutil.mockPageStreamSimple
import pdfcrate.testutil.verifyComponentContext

private const val DEFAULT_SIZE = 500f

@ExtendWith(MockKExtension::class)
class ColumnTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    @Test
    fun singleElement() {
        mockContext()
        val component = mockComponent()
        val contextSlot = slot<ComponentContext>()
        val size = Column(listOf(component)).render(context)
        assertThat(size.width).isEqualTo(10f)
        assertThat(size.height).isEqualTo(10f)
        contextSlot.clear()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }

    @Test
    fun singleElementBlocks() {
        mockContext()
        val component = mockComponent()
        val column = Column(listOf(component))
        val blocks = column.getBlocks(context)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(10f))
        assertThat(blocks.width).isEqualTo(10f)
        val contextSlot = slot<ComponentContext>()
        verify { component.getBlocks(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }

    @Test
    fun multipleEqualElements() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent()
        val size = Column(listOf(component1, component2)).render(context)
        assertThat(size.width).isEqualTo(10f)
        assertThat(size.height).isEqualTo(20f)
        val contextSlot = slot<ComponentContext>()
        verify { component1.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
        contextSlot.clear()
        verify { component2.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 10f)
    }

    @Test
    fun multipleEqualElementsBlocks() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent()
        val size = Column(listOf(component1, component2)).getBlocks(context)
        assertThat(size.width).isEqualTo(10f)
        assertThat(size.heightBlocks).isEqualTo(listOf(10f, 10f))
        val contextSlot = slot<ComponentContext>()
        verify { component1.getBlocks(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
        contextSlot.clear()
        verify { component2.getBlocks(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 10f)
    }

    @Test
    fun multipleDifferentElements() {
        mockContext()
        val component1 = mockComponent(height = 30f)
        val component2 = mockComponent(width = 15f)
        val size = Column(listOf(component1, component2)).render(context)
        assertThat(size.width).isEqualTo(15f)
        assertThat(size.height).isEqualTo(40f)
        val contextSlot = slot<ComponentContext>()
        verify { component1.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
        contextSlot.clear()
        verify { component2.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 30f)
    }

    @Test
    fun multipleDifferentElementsBlocks() {
        mockContext()
        val component1 = mockComponent(height = 30f)
        val component2 = mockComponent(width = 15f)
        val size = Column(listOf(component1, component2)).getBlocks(context)
        assertThat(size.width).isEqualTo(15f)
        assertThat(size.heightBlocks).isEqualTo(listOf(30f, 10f))
        val contextSlot = slot<ComponentContext>()
        verify { component1.getBlocks(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
        contextSlot.clear()
        verify { component2.getBlocks(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 30f)
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