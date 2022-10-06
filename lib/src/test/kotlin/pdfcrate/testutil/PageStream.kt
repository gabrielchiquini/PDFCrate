package pdfcrate.testutil

import io.mockk.every
import org.apache.pdfbox.pdmodel.PDPageContentStream
import pdfcrate.render.ContentStreamWrapper
import pdfcrate.render.PageStream

fun mockPageStreamSimple(pageStream: PageStream, contentStream: PDPageContentStream, size: Float) {
    every { pageStream.contentStreamFor(any(), any()) } returns ContentStreamWrapper(
        stream = contentStream,
        page = 0,
        realOffset = size,
        virtualOffset = 0f,
    )
}
