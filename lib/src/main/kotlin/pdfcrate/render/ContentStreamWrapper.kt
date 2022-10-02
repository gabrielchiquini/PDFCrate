package pdfcrate.render

import org.apache.pdfbox.pdmodel.PDPageContentStream

class ContentStreamWrapper(
    val stream: PDPageContentStream,
    val realOffset: Float,
    val virtualOffset: Float,
    val page: Int
)
