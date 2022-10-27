package pdfcrate.testutil

import io.mockk.every
import io.mockk.mockk
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import pdfcrate.document.Style
import pdfcrate.render.ContentStreamWrapper
import pdfcrate.render.PageStream
import pdfcrate.render.RenderContext
import pdfcrate.util.Edges
import pdfcrate.util.Size

fun mockPageStreamSimple(pageStream: PageStream, contentStream: PDPageContentStream, size: Float) {
    val document = mockk<PDDocument>(relaxed = true)
    every { pageStream.document } returns document
    every { pageStream.contentStreamFor(any(), any()) } returns ContentStreamWrapper(
        stream = contentStream,
        page = 0,
        realOffset = size,
        virtualOffset = 0f,
    )
}

fun mockPageStreamReal(size: Float): PageStream {
    val document = mockk<PDDocument>(relaxed = true)
    return PageStream(document, RenderContext(Style.default(), Edges.ZERO, Size(size, size)))
}
