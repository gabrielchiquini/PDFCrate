package pdfcrate.components

import pdfcrate.document.Style
import pdfcrate.document.TextStyle
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Size


class Paragraph @JvmOverloads constructor(
    private val text: String,
    private val localStyle: TextStyle? = null
) : Component {
    override fun render(style: Style, renderer: ContentBuilder): Size {
        return renderer.writeWrappingText(text, localStyle ?: style.textStyle)
    }
}