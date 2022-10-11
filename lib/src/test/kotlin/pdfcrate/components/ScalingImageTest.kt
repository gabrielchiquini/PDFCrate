package pdfcrate.components

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.assertj.core.api.Assertions
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
class ScalingImageTest {
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
    fun scaleWidthContent() {
        mockContext()
        mockImage()
        val image = ScalingImage.fromWidth(ByteArray(0), "test", 5f)
        val size = image.render(context)
        Assertions.assertThat(size).isEqualTo(Size(5f, 8f))
    }

    @Test
    fun scaleWidthFile() {
        mockContext()
        mockImage()
        val file = mockk<File>(relaxed = true)
        val image = ScalingImage.fromWidth(file, 50f)
        val size = image.render(context)
        Assertions.assertThat(size).isEqualTo(Size(50f, 80f))
        verify { PDImageXObject.createFromFileByContent(eq(file), any()) }
    }

    @Test
    fun scaleHeightContent() {
        mockContext()
        mockImage()
        val image = ScalingImage.fromHeight(ByteArray(0), "test", 60f)
        val size = image.render(context)
        Assertions.assertThat(size).isEqualTo(Size(37.5f, 60f))
    }

    @Test
    fun scaleHeightFile() {
        mockContext()
        mockImage()
        val file = mockk<File>(relaxed = true)
        val image = ScalingImage.fromHeight(file, 20f)
        val size = image.render(context)
        Assertions.assertThat(size).isEqualTo(Size(12.5f, 20f))
        verify { PDImageXObject.createFromFileByContent(eq(file), any()) }
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

    private fun mockImage() {
        every { PDImageXObject.createFromByteArray(any(), any(), any()) } returns imageObject
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        every { imageObject.width } returns 25
        every { imageObject.height } returns 40
    }
}
