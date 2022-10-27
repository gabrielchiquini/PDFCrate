package pdfcrate.components

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
import pdfcrate.testutil.mockPageStreamSimple
import pdfcrate.testutil.verifyComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks


private const val DEFAULT_SIZE = 500f

@ExtendWith(MockKExtension::class)
class FooterTest {
    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    @Test
    fun footerSmallElement() {
        mockContext()
        val component = mockComponent()
        val size = Footer(component).render(context)
        Assertions.assertThat(size.width).isEqualTo(10f)
        Assertions.assertThat(size.height).isEqualTo(DEFAULT_SIZE)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 490f)
    }

    @Test
    fun footerFullSize() {
        mockContext()
        val component = mockComponent(height = DEFAULT_SIZE)
        val size = Footer(component).render(context)
        Assertions.assertThat(size.width).isEqualTo(10f)
        Assertions.assertThat(size.height).isEqualTo(DEFAULT_SIZE)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }

    @Test
    fun footerSmallElementWithMargin() {
        mockContext(Edges(bottom = 15f))
        val component = mockComponent()
        val size = Footer(component).render(context)
        Assertions.assertThat(size.width).isEqualTo(10f)
        Assertions.assertThat(size.height).isEqualTo(485f)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 475f)
    }

    @Test
    fun footerFullSizeWithMargin() {
        mockContext(Edges.all(15f))
        val component = mockComponent(height = 485f)
        val size = Footer(component).render(context)
        Assertions.assertThat(size.width).isEqualTo(10f)
        Assertions.assertThat(size.height).isEqualTo(485f)
        val contextSlot = slot<ComponentContext>()
        verify { component.render(capture(contextSlot)) }
        verifyComponentContext(contextSlot.captured, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }


    private fun mockComponent(width: Float = 10f, height: Float = 10f): SizedComponent {
        val component = mockk<SizedComponent>()
        every { component.getBlocks(any()) } returns SizeBlocks.single(width, height)
        every { component.render(any()) } returns Size(width, height)
        return component
    }

    private fun mockContext(margin: Edges = Edges.ZERO) {
        mockPageStreamSimple(pageStream, contentStream, DEFAULT_SIZE)
        context = ComponentContext(
            pages = pageStream,
            x = 0f,
            maxX = DEFAULT_SIZE,
            y = 0f,
            style = Style.default(),
            renderContext = generateRenderContext(DEFAULT_SIZE, margin),
        )
    }
}
