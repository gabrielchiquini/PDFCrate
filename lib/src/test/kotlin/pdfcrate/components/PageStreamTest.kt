package pdfcrate.components

import org.apache.pdfbox.pdmodel.PDDocument
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pdfcrate.document.Style
import pdfcrate.render.PageStream
import pdfcrate.render.RenderContext
import pdfcrate.util.Edges
import pdfcrate.util.Size

class PageStreamTest {
    private lateinit var document: PDDocument

    @BeforeEach
    fun before() {
        document = PDDocument()
    }

    @AfterEach
    fun after() {
        document.close()
    }

    @Test
    fun offsetSamePageNoMargin() {
        val document = document
        val context = rendererContextNoMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(99f, 1f)
        assertThat(wrapper.page).isEqualTo(0)
        assertThat(wrapper.realOffset).isCloseTo(1f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(99f)
    }

    @Test
    fun offsetPageBreakNoMargin() {
        val document = document
        val context = rendererContextNoMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(99f, 2f)
        assertThat(wrapper.page).isEqualTo(1)
        assertThat(wrapper.realOffset).isCloseTo(100f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(100f)
    }

    @Test
    fun offsetSamePageWithMargin() {
        val document = document
        val context = rendererContextWithMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(79f, 1f)
        assertThat(wrapper.page).isEqualTo(0)
        assertThat(wrapper.realOffset).isCloseTo(11f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(79f)
    }

    @Test
    fun offsetPageBreakWithMargin() {
        val document = document
        val context = rendererContextWithMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(80f, 1f)
        assertThat(wrapper.page).isEqualTo(1)
        assertThat(wrapper.realOffset).isCloseTo(90f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(80f)
    }

    @Test
    fun closeAllStreams() {
        val document = document
        val context = rendererContextWithMargins()
        val stream = PageStream(document, context)
        stream.close()
        assertThat(document.pages).hasSize(1)
    }

    private fun rendererContextNoMargins() = RenderContext(
        style = Style.DEFAULT_STYLE,
        margin = Edges.ZERO,
        pageSize = Size(100f, 100f),
    )

    private fun rendererContextWithMargins() = RenderContext(
        style = Style.DEFAULT_STYLE,
        margin = Edges.all(10f),
        pageSize = Size(100f, 100f),
    )
}
