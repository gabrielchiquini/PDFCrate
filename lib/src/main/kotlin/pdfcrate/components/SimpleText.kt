package pdfcrate.components

import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size
import kotlin.math.max


class SimpleText @JvmOverloads constructor(
    private val lines: List<String>,
    private val localStyle: TextStyle? = null
) : Component {

    @JvmOverloads
    constructor(text: String, localStyle: TextStyle? = null) : this(text.split('\n'), localStyle)

    override fun render(context: ComponentContext): Size {
        val style = localStyle ?: context.style.textStyle
        val font = style.font
        val leading = style.leading * style.fontSize
        var width = 0f
        var currentOffset = context.y
        var wrapper = context.pages.contentStreamFor(currentOffset, leading)
        currentOffset = wrapper.virtualOffset
        var currentPage = wrapper.page
        wrapper.stream.beginText()
        wrapper.stream.setLeading(leading)
        val y = wrapper.realOffset - leading
        wrapper.stream.newLineAtOffset(context.x, y)
        wrapper.stream.setFont(font, style.fontSize)
        lines.forEachIndexed { i, line ->
            val oldWrapper = wrapper
            if (i > 0) {
                wrapper = context.pages.contentStreamFor(currentOffset, leading)
            }
            if (wrapper.page != currentPage) {
                currentPage = wrapper.page
                oldWrapper.stream.endText()
                wrapper.stream.beginText()
                wrapper.stream.newLineAtOffset(context.x, wrapper.realOffset - leading)
                wrapper.stream.setLeading(leading)
                wrapper.stream.setFont(font, style.fontSize)
            }
            wrapper.stream.showText(line)
            width = max(width, textWidth(line, style.font, style.fontSize))
            wrapper.stream.newLine()
            currentOffset = wrapper.virtualOffset + leading
        }
        wrapper.stream.endText()

        return Size(width = width, height = currentOffset - context.y)
    }
}
