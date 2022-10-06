package pdfcrate.components

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.SpacingStyle

private const val DEFAULT_SIZE = 100f
private const val DEFAULT_COMPONENT_WIDTH = 20f
private const val DEFAULT_COMPONENT_HEIGHT = 30f
private val DEFAULT_COMPONENT_SIZE = Size(DEFAULT_COMPONENT_WIDTH, DEFAULT_COMPONENT_HEIGHT)

@ExtendWith(MockKExtension::class)
class ColumnLayoutTest {
    @MockK
    lateinit var pageStream: PageStream

    @Test
    fun singleColumnNoPadding() {
        val component = component()
        val context = componentContext()
        val columnLayout =
            ColumnLayout(listOf(component), listOf(ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 1f)))
        val rendered = columnLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT))
        val innerContext = slot<ComponentContext>()
        verify { component.render(capture(innerContext)) }
        verifyCalledContext(context, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }

    @Test
    fun singleColumnWithPadding() {
        val component = component()
        val context = componentContext()
        val columnLayout = ColumnLayout(
            listOf(component),
            listOf(ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 1f, padding = Edges(1f, 2f, 3f, 4f)))
        )
        val rendered = columnLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext = slot<ComponentContext>()
        verify { component.render(capture(innerContext)) }
        verifyCalledContext(innerContext.captured, x = 4f, maxX = DEFAULT_SIZE - 2f, y = 1f)
    }


    @Test
    fun multipleColumnProportional() {
        val component = component()
        val context = componentContext()
        val columnLayout = ColumnLayout(
            listOf(component, component, component), listOf(
                ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 1f),
                ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 2f, padding = Edges(1f, 2f, 3f, 4f)),
                ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 3f),
            )
        )
        val rendered = columnLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext1 = slot<ComponentContext>()
        val innerContext2 = slot<ComponentContext>()
        val innerContext3 = slot<ComponentContext>()
        verifySequence {
            component.render(capture(innerContext1))
            component.render(capture(innerContext2))
            component.render(capture(innerContext3))
        }
        verifyCalledContext(innerContext1.captured, x = 0f, maxX = 16.66f, y = 0f)
        verifyCalledContext(innerContext2.captured, x = 16.66f + 4f, maxX = 50f - 2f, y = 1f)
        verifyCalledContext(innerContext3.captured, x = 50f, maxX = 100f, y = 0f)
    }

    @Test
    fun multipleColumnAbsolute() {
        val component = component()
        val context = componentContext()
        val columnLayout = ColumnLayout(
            listOf(component, component, component), listOf(
                ColumnConstraints(style = SpacingStyle.ABSOLUTE, value = 30f),
                ColumnConstraints(style = SpacingStyle.ABSOLUTE, value = 30f, padding = Edges(1f, 2f, 3f, 4f)),
                ColumnConstraints(style = SpacingStyle.ABSOLUTE, value = 30f),
            )
        )
        val rendered = columnLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext1 = slot<ComponentContext>()
        val innerContext2 = slot<ComponentContext>()
        val innerContext3 = slot<ComponentContext>()
        verifySequence {
            component.render(capture(innerContext1))
            component.render(capture(innerContext2))
            component.render(capture(innerContext3))
        }
        verifyCalledContext(innerContext1.captured, x = 0f, maxX = 30f, y = 0f)
        verifyCalledContext(innerContext2.captured, x = 34f, maxX = 58f, y = 1f)
        verifyCalledContext(innerContext3.captured, x = 60f, maxX = 90f, y = 0f)
    }

    @Test
    fun multipleColumnMixed() {
        val component = component()
        val context = componentContext()
        val columnLayout = ColumnLayout(
            listOf(component, component, component), listOf(
                ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 1f, padding = Edges(1f, 2f, 3f, 4f)),
                ColumnConstraints(style = SpacingStyle.ABSOLUTE, value = 40f),
                ColumnConstraints(style = SpacingStyle.PROPORTIONAL, value = 3f),
            )
        )
        val rendered = columnLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext1 = slot<ComponentContext>()
        val innerContext2 = slot<ComponentContext>()
        val innerContext3 = slot<ComponentContext>()
        verifySequence {
            component.render(capture(innerContext1))
            component.render(capture(innerContext2))
            component.render(capture(innerContext3))
        }
        verifyCalledContext(innerContext1.captured, x = 4f, maxX = 13f, y = 1f)
        verifyCalledContext(innerContext2.captured, x = 15f, maxX = 55f, y = 0f)
        verifyCalledContext(innerContext3.captured, x = 55f, maxX = 100f, y = 0f)
    }

    private fun component(): Component {
        val component = mockk<Component>()
        every { component.render(any()) } returns DEFAULT_COMPONENT_SIZE
        return component
    }

    private fun verifyCalledContext(context: ComponentContext, x: Float, maxX: Float, y: Float) {
        val offset = Offset.offset(0.1f)
        assertThat(context.x).isCloseTo(x, offset)
        assertThat(context.maxX).isCloseTo(maxX, offset)
        assertThat(context.y).isCloseTo(y, offset)
    }


    private fun componentContext() = ComponentContext(
        x = 0f,
        maxX = DEFAULT_SIZE,
        y = 0f,
        style = Style.DEFAULT_STYLE,
        pages = pageStream,
    )
}
