package pdfcrate.render

import org.apache.pdfbox.pdmodel.PDDocument
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.Test
import pdfcrate.document.Style
import pdfcrate.util.Edges
import pdfcrate.util.Size

class PageStreamTest {

    @Test
    fun offsetSamePageNoMargin() {
        val document = PDDocument()
        val context = rendererContextNoMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(99f, 1f)
        assertThat(wrapper.page).isEqualTo(0)
        assertThat(wrapper.realOffset).isCloseTo(1f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(99f)
    }

    @Test
    fun offsetPageBreakNoMargin() {
        val document = PDDocument()
        val context = rendererContextNoMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(99f, 2f)
        assertThat(wrapper.page).isEqualTo(1)
        assertThat(wrapper.realOffset).isCloseTo(100f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(100f)
    }

    @Test
    fun offsetSamePageWithMargin() {
        val document = PDDocument()
        val context = rendererContextWithMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(79f, 1f)
        assertThat(wrapper.page).isEqualTo(0)
        assertThat(wrapper.realOffset).isCloseTo(11f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(79f)
    }

    @Test
    fun offsetPageBreakWithMargin() {
        val document = PDDocument()
        val context = rendererContextWithMargins()
        val stream = PageStream(document, context)
        val wrapper = stream.contentStreamFor(80f, 1f)
        assertThat(wrapper.page).isEqualTo(1)
        assertThat(wrapper.realOffset).isCloseTo(90f, Offset.offset(0.1f))
        assertThat(wrapper.virtualOffset).isEqualTo(80f)
    }

    @Test
    fun closeAllStreams() {
        val document = PDDocument()
        val context = rendererContextWithMargins()
        val stream = PageStream(document, context)
        stream.close()
        assertThat(document.pages).hasSize(1)
    }

    private fun rendererContextNoMargins() = RendererContext(
        style = Style.DEFAULT_STYLE,
        margin = Edges.ZERO,
        pageSize = Size(100f, 100f),
    )

    private fun rendererContextWithMargins() = RendererContext(
        style = Style.DEFAULT_STYLE,
        margin = Edges.all(10f),
        pageSize = Size(100f, 100f),
    )
}
