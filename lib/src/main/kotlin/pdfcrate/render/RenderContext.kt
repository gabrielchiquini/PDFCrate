package pdfcrate.render

import pdfcrate.document.Style
import pdfcrate.util.Edges
import pdfcrate.util.Size

class RenderContext(val style: Style, val margin: Edges, val pageSize: Size) {
    fun contentHeight() = pageSize.height - margin.top - margin.bottom
}
