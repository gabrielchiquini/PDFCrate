package pdfcrate.components

import pdfcrate.document.TextStyle
import pdfcrate.render.Alignment
import pdfcrate.render.ComponentContext
import pdfcrate.util.SizeBlocks
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size
import kotlin.math.min


class FlexibleText @JvmOverloads constructor(
    private val text: String,
    private val align: Alignment,
    private val localStyle: TextStyle? = null,
) : SizedComponent {
    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val style = localStyle ?: context.style.textStyle
        val size = fontSize(context, style)
        val leading = style.leading * size
        return when (align) {
            Alignment.LEFT -> SizeBlocks.single(textWidth(text, style.font, size), leading)
            Alignment.CENTER -> SizeBlocks.single(context.width(), leading)
        }
    }

    override fun render(context: ComponentContext): Size {
        val style = localStyle ?: context.style.textStyle
        val font = style.font
        val size = fontSize(context, style)
        val leading = style.leading * size
        val textWidth = style.font.getStringWidth(text) * size / 1000
        val wrapper = context.pages.contentStreamFor(context.y, leading)
        wrapper.stream.beginText()
        val (newX, usedWidth) = when (align) {
            Alignment.LEFT -> {
                Pair(context.x, textWidth(text, font, size))
            }

            Alignment.CENTER -> {
                Pair((context.width() - textWidth) / 2, context.maxX - context.x)
            }
        }
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
