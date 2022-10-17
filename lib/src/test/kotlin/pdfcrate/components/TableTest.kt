package pdfcrate.components

import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
import pdfcrate.testutil.mockPageStreamReal
import pdfcrate.testutil.verifyComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

private const val DEFAULT_SIZE = 500f
private const val DEFAULT_COMPONENT_SIZE = 10f

@ExtendWith(MockKExtension::class)
class TableTest {
    lateinit var pageStream: PageStream

    private lateinit var context: ComponentContext


    @Test
    fun testSingleColumn() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1), arrayOf(component2)),
            arrayOf(TableColumn.proportional(1f))
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), DEFAULT_COMPONENT_SIZE * 2))
        verifyRender(component1, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
        verifyRender(component2, x = 0f, maxX = DEFAULT_SIZE, y = DEFAULT_COMPONENT_SIZE)
    }

    @Test
    fun testSingleColumnWithPadding() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1), arrayOf(component2)),
            arrayOf(
                TableColumn.proportional(1f, Edges(1f, 2f, 3f, 4f))
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), DEFAULT_COMPONENT_SIZE * 2 + 4 * 2))
        verifyRender(component1, x = 4f, maxX = DEFAULT_SIZE - 2f, y = 1f)
        verifyRender(component2, x = 4f, maxX = DEFAULT_SIZE - 2f, y = 15f)
    }

    @Test
    fun testColumnProportional() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1, component2)),
            arrayOf(
                TableColumn.proportional(2f),
                TableColumn.proportional(3f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), DEFAULT_COMPONENT_SIZE))
        verifyRender(component1, x = 0f, maxX = 200f, y = 0f)
        verifyRender(component2, x = 200f, maxX = DEFAULT_SIZE, y = 0f)
    }

    @Test
    fun testColumnAbsolute() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1, component2)),
            arrayOf(
                TableColumn.absolute(20f),
                TableColumn.absolute(30f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), DEFAULT_COMPONENT_SIZE))
        verifyRender(component1, x = 0f, maxX = 20f, y = 0f)
        verifyRender(component2, x = 20f, maxX = 50f, y = 0f)
    }

    @Test
    fun testColumnShrink() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent(height = 20f)
        val component3 = mockComponent(width = 15f)
        val component4 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1, component2), arrayOf(component3, component4)),
            arrayOf(
                TableColumn.shrink(),
                TableColumn.proportional(1f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), 30f))
        verifyRender(component1, x = 0f, maxX = 15f, y = 0f)
        verifyRender(component2, x = 15f, maxX = DEFAULT_SIZE, y = 0f)
        verifyRender(component3, x = 0f, maxX = 15f, y = 20f)
        verifyRender(component4, x = 15f, maxX = DEFAULT_SIZE, y = 20f)
    }

    @Test
    fun testColumnShrinkWithPadding() {
        mockContext()
        val component1 = mockComponent(width = 15f)
        val component2 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1, component2)),
            arrayOf(
                TableColumn.shrink(Edges(1f, 2f, 3f, 4f)),
                TableColumn.proportional(1f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), 14f))
        verifyRender(component1, x = 4f, maxX = 19f, y = 1f)
        verifyRender(component2, x = 21f, maxX = DEFAULT_SIZE, y = 0f)
    }


    @Test
    fun testRowPageLimits() {
        mockContext()
        val component1 = mockComponent(height = 499f, width = 499f)
        val component2 = mockComponent()
        val component3 = mockComponent(height = 1f, width = 1f)
        val component4 = mockComponent(height = 1f, width = 1f)
        val size = Table(
            arrayOf(arrayOf(component1, component2), arrayOf(component3, component4)),
            arrayOf(
                TableColumn.shrink(),
                TableColumn.proportional(1f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), 500f))
        verifyRender(component1, x = 0f, maxX = 499f, y = 0f)
        verifyRender(component2, x = 499f, maxX = DEFAULT_SIZE, y = 0f)
        verifyRender(component3, x = 0f, maxX = 499f, y = 499f)
        verifyRender(component4, x = 499f, maxX = DEFAULT_SIZE, y = 499f)
    }

    @Test
    fun testRowPageBreak() {
        mockContext()
        val component1 = mockComponent(height = 499f)
        val component2 = mockComponent()
        val component3 = mockComponent(height = 2f)
        val component4 = mockComponent(height = 1f)
        val size = Table(
            arrayOf(arrayOf(component1, component2), arrayOf(component3, component4)),
            arrayOf(
                TableColumn.absolute(100f),
                TableColumn.proportional(1f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), 502f))
        verifyRender(component1, x = 0f, maxX = 100f, y = 0f)
        verifyRender(component2, x = 100f, maxX = DEFAULT_SIZE, y = 0f)
        verifyRender(component3, x = 0f, maxX = 100f, y = DEFAULT_SIZE)
        verifyRender(component4, x = 100f, maxX = DEFAULT_SIZE, y = DEFAULT_SIZE)
    }

    @Test
    fun testMixedColumns() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent(width = 50f)
        val component3 = mockComponent()
        val component4 = mockComponent(height = 45f)
        val component5 = mockComponent()
        val size = Table(
            arrayOf(arrayOf(component1, component2, component3, component4, component5)),
            arrayOf(
                TableColumn.absolute(150f),
                TableColumn.shrink(),
                TableColumn.absolute(100f),
                TableColumn.proportional(1f),
                TableColumn.proportional(3f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), 45f))
        verifyRender(component1, x = 0f, maxX = 150f, y = 0f)
        verifyRender(component2, x = 150f, maxX = 200f, y = 0f)
        verifyRender(component3, x = 200f, maxX = 300f, y = 0f)
        verifyRender(component4, x = 300f, maxX = 350f, y = 0f)
        verifyRender(component5, x = 350f, maxX = 500f, y = 0f)
    }

    @Test
    fun testListConversion() {
        mockContext()
        val component1 = mockComponent()
        val component2 = mockComponent(width = 50f)
        val component3 = mockComponent()
        val component4 = mockComponent(height = 45f)
        val component5 = mockComponent()
        val size = Table(
            listOf(listOf(component1, component2, component3, component4, component5)),
            listOf(
                TableColumn.absolute(150f),
                TableColumn.shrink(),
                TableColumn.absolute(100f),
                TableColumn.proportional(1f),
                TableColumn.proportional(3f)
            )
        ).render(context)
        assertThat(size).isEqualTo(Size(context.width(), 45f))
        verifyRender(component1, x = 0f, maxX = 150f, y = 0f)
        verifyRender(component2, x = 150f, maxX = 200f, y = 0f)
        verifyRender(component3, x = 200f, maxX = 300f, y = 0f)
        verifyRender(component4, x = 300f, maxX = 350f, y = 0f)
        verifyRender(component5, x = 350f, maxX = 500f, y = 0f)
    }

    private fun verifyRender(component: SizedComponent, x: Float, maxX: Float, y: Float) {
        val componentContext = slot<ComponentContext>()
        verify(atLeast = 1) {
            component.getBlocks(any())
        }
        verify {
            component.render(capture(componentContext))
        }
        verifyComponentContext(context = componentContext.captured, x = x, y = y, maxX = maxX)
    }

    private fun mockComponent(
        width: Float = DEFAULT_COMPONENT_SIZE,
        height: Float = DEFAULT_COMPONENT_SIZE
    ): SizedComponent {
        pageStream = mockPageStreamReal(DEFAULT_SIZE)
        val component = mockk<SizedComponent>()
        every { component.getBlocks(any()) } returns SizeBlocks.single(width, height)
        every { component.render(any()) } returns Size(width, height)
        return component
    }

    private fun mockContext() {
        pageStream = mockPageStreamReal(DEFAULT_SIZE)
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
