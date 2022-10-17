package pdfcrate.components

import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.util.RenderingUtil.Companion.WHITESPACE_REGEX
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

/**
 * Creates a block of text aligned to the left using the whole width of the parent, breaking lines when needed
 */
class Paragraph @JvmOverloads constructor(
    private val text: String, private val localStyle: TextStyle? = null
) : SizedComponent {
    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val lines = getLines(context)
        return SimpleText(lines, getStyle(context)).getBlocks(context)
    }

    override fun render(context: ComponentContext): Size {
        val lines = getLines(context)
        return SimpleText(lines, getStyle(context)).render(context)
    }

    private fun getLines(
        context: ComponentContext
    ): MutableList<String> {
        val style = getStyle(context)
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
        return lines
    }

    private fun getStyle(context: ComponentContext) = localStyle ?: context.style.textStyle
}
