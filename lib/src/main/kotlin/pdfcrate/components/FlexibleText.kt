package pdfcrate.components

import pdfcrate.document.TextStyle
import pdfcrate.render.Alignment
import pdfcrate.render.ComponentContext
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size
import kotlin.math.min


class FlexibleText @JvmOverloads constructor(
    private val text: String,
    private val align: Alignment,
    private val localStyle: TextStyle? = null,
) : Component {
    override fun render(context: ComponentContext): Size {
        val style = localStyle ?: context.style.textStyle
        val font = style.font
        val width = context.maxX - context.x
        val maxSize = width * 1000 / font.getStringWidth(text)
        val size = min(style.fontSize, maxSize)
        val leading = style.leading * size
        val wrapper = context.pages.contentStreamFor(context.y, leading)
        wrapper.stream.beginText()
        val y = wrapper.realOffset - leading
        val textWidth = style.font.getStringWidth(text) * size / 1000
        val (newX, usedWidth) = when (align) {
            Alignment.LEFT -> {
                Pair(context.x, textWidth(text, font, size))
            }
            Alignment.CENTER -> {
                Pair((width - textWidth) / 2, context.maxX - context.x)
            }
        }
        wrapper.stream.newLineAtOffset(newX, y)
        wrapper.stream.setFont(font, size)
        wrapper.stream.showText(text)
        wrapper.stream.endText()
        return Size(width = usedWidth, height = wrapper.virtualOffset + leading - context.y)
    }

}
