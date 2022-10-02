package pdfcrate.components

import pdfcrate.document.Style
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Size

interface Component {
    fun render(style: Style, renderer: ContentBuilder): Size
}
