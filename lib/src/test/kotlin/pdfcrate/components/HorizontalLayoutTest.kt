package pdfcrate.components

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
import pdfcrate.testutil.verifyComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size

private const val DEFAULT_SIZE = 100f
private const val DEFAULT_COMPONENT_WIDTH = 20f
private const val DEFAULT_COMPONENT_HEIGHT = 30f
private val DEFAULT_COMPONENT_SIZE = Size(DEFAULT_COMPONENT_WIDTH, DEFAULT_COMPONENT_HEIGHT)

@ExtendWith(MockKExtension::class)
class HorizontalLayoutTest {
    @MockK
    lateinit var pageStream: PageStream

    @Test
    fun singleColumnNoPadding() {
        val component = component()
        val context = componentContext()
        val horizontalLayout =
            HorizontalLayout(
                listOf(HorizontalLayoutColumn.proportional(size = 1f, child = component))
            )
        val rendered = horizontalLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT))
        val innerContext = slot<ComponentContext>()
        verify { component.render(capture(innerContext)) }
        verifyComponentContext(context, x = 0f, maxX = DEFAULT_SIZE, y = 0f)
    }

    @Test
    fun singleColumnWithPadding() {
        val component = component()
        val context = componentContext()
        val horizontalLayout = HorizontalLayout(
            listOf(
                HorizontalLayoutColumn.proportional(
                    size = 1f,
                    padding = Edges(1f, 2f, 3f, 4f),
                    child = component
                )
            )
        )
        val rendered = horizontalLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext = slot<ComponentContext>()
        verify { component.render(capture(innerContext)) }
        verifyComponentContext(innerContext.captured, x = 4f, maxX = DEFAULT_SIZE - 2f, y = 1f)
    }


    @Test
    fun multipleColumnProportional() {
        val component = component()
        val context = componentContext()
        val horizontalLayout = HorizontalLayout(
            listOf(
                HorizontalLayoutColumn.proportional(size = 1f, child = component),
                HorizontalLayoutColumn.proportional(
                    size = 2f,
                    padding = Edges(1f, 2f, 3f, 4f),
                    child = component
                ),
                HorizontalLayoutColumn.proportional(size = 3f, child = component),
            )
        )
        val rendered = horizontalLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext1 = slot<ComponentContext>()
        val innerContext2 = slot<ComponentContext>()
        val innerContext3 = slot<ComponentContext>()
        verifySequence {
            component.render(capture(innerContext1))
            component.render(capture(innerContext2))
            component.render(capture(innerContext3))
        }
        verifyComponentContext(innerContext1.captured, x = 0f, maxX = 16.66f, y = 0f)
        verifyComponentContext(innerContext2.captured, x = 16.66f + 4f, maxX = 50f - 2f, y = 1f)
        verifyComponentContext(innerContext3.captured, x = 50f, maxX = 100f, y = 0f)
    }

    @Test
    fun multipleColumnAbsolute() {
        val component = component()
        val context = componentContext()
        val horizontalLayout = HorizontalLayout(
            listOf(
                HorizontalLayoutColumn.absolute(size = 30f, child = component),
                HorizontalLayoutColumn.absolute(
                    size = 30f,
                    padding = Edges(1f, 2f, 3f, 4f),
                    child = component
                ),
                HorizontalLayoutColumn.absolute(size = 30f, child = component),
            )
        )
        val rendered = horizontalLayout.render(context)
        assertThat(rendered).isEqualTo(Size(DEFAULT_SIZE, DEFAULT_COMPONENT_HEIGHT + 4f))
        val innerContext1 = slot<ComponentContext>()
        val innerContext2 = slot<ComponentContext>()
        val innerContext3 = slot<ComponentContext>()
        verifySequence {
            component.render(capture(innerContext1))
            component.render(capture(innerContext2))
            component.render(capture(innerContext3))
        }
        verifyComponentContext(innerContext1.captured, x = 0f, maxX = 30f, y = 0f)
        verifyComponentContext(innerContext2.captured, x = 34f, maxX = 58f, y = 1f)
        verifyComponentContext(innerContext3.captured, x = 60f, maxX = 90f, y = 0f)
    }

    private fun component(): Component {
        val component = mockk<Component>()
        every { component.render(any()) } returns DEFAULT_COMPONENT_SIZE
        return component
    }


    private fun componentContext() = ComponentContext(
        x = 0f,
        maxX = DEFAULT_SIZE,
        y = 0f,
        style = Style.DEFAULT_STYLE,
        pages = pageStream,
        renderContext = generateRenderContext(DEFAULT_SIZE),
    )
}
