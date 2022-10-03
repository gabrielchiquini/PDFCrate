package pdfcrate.components

import pdfcrate.document.Style
import pdfcrate.document.TextStyle
import pdfcrate.render.Alignment
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Size


class FlexibleText @JvmOverloads constructor(
    private val text: String,
    private val align: Alignment,
    private val localStyle: TextStyle? = null,
) : Component {
    override fun render(style: Style, renderer: ContentBuilder): Size {
        return renderer.writeFlexSizeText(text, localStyle ?: style.textStyle, align)
    }
}