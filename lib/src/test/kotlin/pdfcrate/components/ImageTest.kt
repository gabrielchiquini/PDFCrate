package pdfcrate.components

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.mockPageStreamSimple
import pdfcrate.util.Size
import java.io.File

private const val DEFAULT_SIZE = 500f

@ExtendWith(MockKExtension::class)
class ImageTest {
    @MockK(relaxed = true)
    lateinit var imageObject: PDImageXObject

    @MockK
    lateinit var pageStream: PageStream

    @MockK(relaxed = true)
    lateinit var contentStream: PDPageContentStream

    private lateinit var context: ComponentContext

    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            mockkStatic(PDImageXObject::createFromByteArray)
            mockkStatic(PDImageXObject::createFromFileByContent)
        }

        @JvmStatic
        @AfterAll
        fun after() {
            unmockkAll()
        }
    }

    @Test
    fun simpleImageFromByteArray() {
        mockContext()
        every { PDImageXObject.createFromByteArray(any(), any(), any()) } returns imageObject
        val image = Image.fromContent(ByteArray(0), "test.jpg", Size(10f, 10f))
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(10f, 10f))
        verify { PDImageXObject.createFromByteArray(any(), any(), eq("test.jpg")) }
    }

    @Test
    fun simpleImageFromFile() {
        mockContext()
        val file = mockk<File>(relaxed = true)
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        val image = Image.fromFile(file, Size(10f, 10f))
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(10f, 10f))
        verify { PDImageXObject.createFromFileByContent(eq(file), any()) }
    }

    @Test
    fun simpleImageFromPath() {
        mockContext()
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        val image = Image.fromPath("test", Size(10f, 10f))
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(10f, 10f))
        verify { PDImageXObject.createFromFileByContent(any(), any()) }
    }

    @Test
    fun calculateImageSize() {
        mockContext()
        val image = Image.fromPath("test", Size(10f, 10f))
        val size = image.getBlocks(context)
        assertThat(size.width).isEqualTo(10f)
        assertThat(size.heightBlocks).isEqualTo(listOf(10f))
    }

    private fun mockContext() {
        mockPageStreamSimple(pageStream, contentStream, DEFAULT_SIZE)
        context = ComponentContext(
            pages = pageStream,
            x = 0f,
            maxX = DEFAULT_SIZE,
            y = 0f,
            style = Style.DEFAULT_STYLE
        )
    }
}
