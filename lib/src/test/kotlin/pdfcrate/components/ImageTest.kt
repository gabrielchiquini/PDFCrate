package pdfcrate.components

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.testutil.generateRenderContext
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

    @BeforeEach
    fun before() {
        mockkStatic(PDImageXObject::createFromByteArray)
        mockkStatic(PDImageXObject::createFromFileByContent)
    }

    @AfterEach
    fun after() {
        unmockkAll()
    }

    @Test
    fun scaleWidthContent() {
        mockContext()
        mockImage()
        val image = Image.fromWidth(ByteArray(0), "test", 5f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(5f, 8f))
    }

    @Test
    fun scaleWidthFile() {
        mockContext()
        mockImage()
        val file = mockk<File>(relaxed = true)
        val image = Image.fromWidth(file, 50f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(50f, 80f))
        verify { PDImageXObject.createFromFileByContent(eq(file), any()) }
    }

    @Test
    fun scaleHeightContent() {
        mockContext()
        mockImage()
        val image = Image.fromHeight(ByteArray(0), "test", 60f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(37.5f, 60f))
    }

    @Test
    fun scaleHeightFile() {
        mockContext()
        mockImage()
        val file = mockk<File>(relaxed = true)
        val image = Image.fromHeight(file, 20f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(12.5f, 20f))
        verify { PDImageXObject.createFromFileByContent(eq(file), any()) }
    }

    @Test
    fun scaleHeightWithCallToBlocks() {
        mockContext()
        mockImage()
        val file = mockk<File>(relaxed = true)
        val image = Image.fromHeight(file, 20f)
        val blocks = image.getBlocks(context)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(12.5f, 20f))
        assertThat(blocks.width).isEqualTo(12.5f)
        assertThat(blocks.heightBlocks).isEqualTo(listOf(20f))
        verify(exactly = 1) { PDImageXObject.createFromFileByContent(eq(file), any()) }
    }


    @Test
    fun manualScalingFromFile() {
        mockContext()
        val file = mockk<File>(relaxed = true)
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        val image = Image.scale(file, 10f, 10f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(10f, 10f))
        verify { PDImageXObject.createFromFileByContent(eq(file), any()) }
    }

    @Test
    fun manualScalingFromPath() {
        mockContext()
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        val image = Image.scale("test", 10f, 10f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(10f, 10f))
        verify { PDImageXObject.createFromFileByContent(any(), any()) }
    }

    @Test
    fun manualScalingFromByteArray() {
        mockContext()
        every { PDImageXObject.createFromByteArray(any(), any(), any()) } returns imageObject
        val image = Image.scale(ByteArray(0), "test.jpg", 10f, 10f)
        val size = image.render(context)
        assertThat(size).isEqualTo(Size(10f, 10f))
        verify { PDImageXObject.createFromByteArray(any(), any(), eq("test.jpg")) }
    }

    @Test
    fun manualScalingSize() {
        mockContext()
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        val image = Image.scale("test.png", 10f, 10f)
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
            style = Style.default(),
            renderContext = generateRenderContext(DEFAULT_SIZE),
        )
    }

    private fun mockImage() {
        every { PDImageXObject.createFromByteArray(any(), any(), any()) } returns imageObject
        every { PDImageXObject.createFromFileByContent(any(), any()) } returns imageObject
        every { imageObject.width } returns 25
        every { imageObject.height } returns 40
    }
}
