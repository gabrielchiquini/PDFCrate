package pdfcrate.components

import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.util.RenderingUtil.Companion.WHITESPACE_REGEX
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size


class Paragraph @JvmOverloads constructor(
    private val text: String,
    private val localStyle: TextStyle? = null
) : Component {
    override fun render(context: ComponentContext): Size {
        val style = localStyle ?: context.style.textStyle
        val words = text.split(WHITESPACE_REGEX)
        val lineSize = context.maxX - context.x
        val builder = StringBuilder()
        val lines = mutableListOf<String>()
        words.forEach {
            if (builder.isNotEmpty()) {
                builder.append(' ')
            }
            builder.append(it)
            val line = builder.toString()
            if (textWidth(line, style.font, style.fontSize) > lineSize) {
                builder.delete(builder.length - it.length - 1, builder.length)
                lines.add(builder.toString())
                builder.setLength(0)
                builder.append(it)
            }
        }
        if (builder.isNotEmpty()) {
            lines.add(builder.toString())
        }
        return SimpleText(lines, style).render(context)
    }
}
