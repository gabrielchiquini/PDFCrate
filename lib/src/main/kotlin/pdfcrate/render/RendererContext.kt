package pdfcrate.render

import pdfcrate.document.Style
import pdfcrate.util.Size

class RendererContext(val style: Style, val margin: Size, val pageSize: Size) {
    fun contentHeight() = pageSize.height - margin.height * 2
}