package pdfcrate.components

import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import kotlin.math.min

/**
 * Renders a single-line block of text, reducing font size to fit in the container width, if needed
 *
 * If the text contains a line break, it will give an error in the rendering process
 */
class FlexibleText @JvmOverloads constructor(
    private val text: String,
    private val localStyle: TextStyle? = null,
) : SizedComponent {
    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val style = localStyle ?: context.style.textStyle
        val size = fontSize(context, style)
        val leading = style.leading * style.fontSize
        return SizeBlocks.single(textWidth(text, style.font, size), leading)
    }

    override fun render(context: ComponentContext): Size {
        val style = localStyle ?: context.style.textStyle
        val font = style.font
        val size = fontSize(context, style)
        // using desired font size leading for better appeareance in inline styles
        val leading = style.leading * style.fontSize
        val wrapper = context.pages.contentStreamFor(context.y, leading)
        wrapper.stream.beginText()
        val newX = context.x
        val usedWidth = textWidth(text, font, size)
        val y = wrapper.realOffset - leading
        wrapper.stream.newLineAtOffset(newX, y)
        wrapper.stream.setFont(font, size)
        wrapper.stream.showText(text)
        wrapper.stream.endText()
        return Size(width = usedWidth, height = wrapper.virtualOffset + leading - context.y)
    }

    private fun fontSize(
        context: ComponentContext, style: TextStyle
    ): Float {
        val maxSize = (context.width()) * 1000 / style.font.getStringWidth(text)
        return min(style.fontSize, maxSize)
    }
}
