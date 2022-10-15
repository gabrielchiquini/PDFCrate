package pdfcrate.document

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.pdfbox.pdmodel.PDDocument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.components.Component
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.util.Edges
import pdfcrate.util.Size
import java.io.OutputStream

private const val DEFAULT_SIZE = 100f

@ExtendWith(MockKExtension::class)
class DocumentTest {
    @MockK(relaxed = true)
    lateinit var outputStream: OutputStream

    @MockK
    lateinit var component: Component

    companion object {
        @JvmStatic
        @BeforeAll
        fun mockTypes() {
            mockkConstructor(PDDocument::class)
            mockkConstructor(PageStream::class)
        }

        @JvmStatic
        @AfterAll
        fun unMockTypes() {
            unmockkAll()
        }
    }

    @Test
    fun renderSingleElement() {
        mockBasicComponent()
        val document = Document()
        document.size(Size(DEFAULT_SIZE, DEFAULT_SIZE))
            .margin(Edges.symmetric(10f, 10f))
        document.add(component)
        document.render(outputStream)
        val context = slot<ComponentContext>()
        verify { component.render(capture(context)) }
        verifyCalledContext(context.captured, x = 10f, maxX = 90f, y = 0f)
    }

    @Test
    fun renderTwoElements() {
        mockBasicComponent()
        val document = Document()
        document.size(Size(DEFAULT_SIZE, DEFAULT_SIZE)).margin(Edges.symmetric(10f, 10f))
        document.add(component)
        document.add(component)
        document.render(outputStream)
        val firstContext = slot<ComponentContext>()
        val secondContext = slot<ComponentContext>()
        verifySequence { component.render(capture(firstContext)); component.render(capture(secondContext)) }
        verifyCalledContext(firstContext.captured, x = 10f, maxX = 90f, y = 0f)
        verifyCalledContext(secondContext.captured, x = 10f, maxX = 90f, y = 10f)
    }


    @Test
    fun renderTwoElementsNoMargin() {
        mockBasicComponent()
        val document = Document()
        document.size(Size(DEFAULT_SIZE, DEFAULT_SIZE))
        document.add(component)
        document.add(component)
        document.render(outputStream)
        val firstContext = slot<ComponentContext>()
        val secondContext = slot<ComponentContext>()
        verifySequence {
            component.render(capture(firstContext))
            component.render(capture(secondContext))
        }
        verifyCalledContext(firstContext.captured, x = 0f, maxX = 100f, y = 0f)
        verifyCalledContext(secondContext.captured, x = 0f, maxX = 100f, y = 10f)
    }

    private fun mockBasicComponent() {
        every { component.render(any()) } returns Size(10f, 10f)
    }

    private fun verifyCalledContext(context: ComponentContext, x: Float, maxX: Float, y: Float) {
        assertThat(context.x).isEqualTo(x)
        assertThat(context.maxX).isEqualTo(maxX)
        assertThat(context.y).isEqualTo(y)
    }
}
