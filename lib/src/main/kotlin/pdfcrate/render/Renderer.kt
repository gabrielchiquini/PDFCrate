package pdfcrate.render

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import pdfcrate.components.Component
import java.io.OutputStream
import kotlin.math.floor

class Renderer(private val context: RendererContext, private val components: MutableList<Component>) {

    internal fun render(outputStream: OutputStream) {
        val document = PDDocument()
        val pages = PageStream(document, context)
        var virtualY = 0f
        components.forEach { component ->
            val content = ContentBuilder(
                pages = pages,
                x = context.margin.left,
                maxX = context.pageSize.width - context.margin.right,
                startingY = virtualY,
            )
            val box = component.render(context.style, content)
            virtualY += box.height
        }
        pages.close()
        document.save(outputStream)
    }
}

class PageStream(private val document: PDDocument, private val context: RendererContext) {
    private val streams: MutableList<PDPageContentStream> = mutableListOf()
    private val pages: MutableList<PDPage> = mutableListOf()

    init {
        newPage()
    }

    fun contentStreamFor(virtualY: Float, height: Float): ContentStreamWrapper {
        val contentHeight = context.contentHeight()
        val pageHeight = context.pageSize.height
        val page = floor(virtualY / contentHeight).toInt()
        val offsetFromPage = virtualY % contentHeight

        if (offsetFromPage + height > contentHeight) {
            val newPage = page + 1
            assertPageSize(newPage)
            return ContentStreamWrapper(
                stream = streams[newPage],
                realOffset = pageHeight - context.margin.top,
                virtualOffset = newPage * contentHeight,
                page = newPage,
            )
        }
        assertPageSize(page)
        return ContentStreamWrapper(
            stream = streams[page],
            realOffset = pageHeight - context.margin.top - offsetFromPage,
            virtualOffset = virtualY,
            page = page,
        )
    }

    private fun assertPageSize(newPage: Int) {
        for (i in 0 until newPage + 1 - pages.size) {
            newPage()
        }
    }

    private fun newPage() {
        val page = PDPage(PDRectangle(context.pageSize.width, context.pageSize.height))
        pages.add(page)
        streams.add(PDPageContentStream(document, page))
    }

    fun close() {
        streams.forEach { it.close() }
        pages.forEach { document.addPage(it) }
    }
}
