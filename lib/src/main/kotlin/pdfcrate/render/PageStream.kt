package pdfcrate.render

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import kotlin.math.floor

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