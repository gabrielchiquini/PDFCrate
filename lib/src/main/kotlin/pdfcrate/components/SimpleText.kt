package pdfcrate.components

import pdfcrate.document.Style
import pdfcrate.document.TextStyle
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Size


class SimpleText @JvmOverloads constructor(
    private val text: String,
    private val localStyle: TextStyle? = null
) : Component {
    override fun render(style: Style, renderer: ContentBuilder): Size {
        return renderer.writeTextLines(text, localStyle ?: style.textStyle)
    }
}