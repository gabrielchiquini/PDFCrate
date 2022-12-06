package pdfcrate.components

import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import pdfcrate.document.TextStyle
import pdfcrate.render.ComponentContext
import pdfcrate.util.RenderingUtil.Companion.textWidth
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

/**
 * Adds a multi-line block of text to the document
 *
 * This component does not soft-wrap lines and will exceed the parent component if too big,
 * but it is faster. If you need soft-wrap, use [Paragraph]
 */
class Text(
    private val lines: List<String>,
    private val align: TextAlign = TextAlign.LEFT,
    private val localStyle: TextStyle? = null,
) : SizedComponent {
    @JvmOverloads
    constructor(text: String, localStyle: TextStyle? = null) : this(text.lines(), localStyle)

    @JvmOverloads
    constructor(lines: List<String>, localStyle: TextStyle? = null) : this(lines, TextAlign.LEFT, localStyle)

    companion object {
        @JvmOverloads
        @JvmStatic
        fun justify(lines: List<String>, localStyle: TextStyle? = null) = Text(lines, TextAlign.JUSTIFIED, localStyle)

        @JvmOverloads
        @JvmStatic
        fun justify(text: String, localStyle: TextStyle? = null) = Text(text.lines(), TextAlign.JUSTIFIED, localStyle)

        @JvmOverloads
        @JvmStatic
        fun rightAlign(lines: List<String>, localStyle: TextStyle? = null) = Text(lines, TextAlign.RIGHT, localStyle)

        @JvmOverloads
        @JvmStatic
        fun rightAlign(text: String, localStyle: TextStyle? = null) = Text(text.lines(), TextAlign.RIGHT, localStyle)
    }

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val style = localStyle ?: context.style.textStyle
        val leading = style.leading * style.fontSize
        val width = componentWidth(context)
        return SizeBlocks(width, List(lines.size) { leading })
    }

    private fun componentWidth(
        context: ComponentContext
    ): Float {
        val style = localStyle ?: context.style.textStyle
        val font = style.font
        return when (align) {
            TextAlign.LEFT -> getMaxLineSize(font, style)
            TextAlign.JUSTIFIED -> if (lines.size > 1) {
                context.width()
            } else {
                getMaxLineSize(font, style)
            }

            TextAlign.RIGHT -> context.width()
        }
    }

    override fun render(context: ComponentContext): Size {
        val style = localStyle ?: context.style.textStyle
        val font = style.font
        val width = componentWidth(context)
        val leading = style.leading * style.fontSize
        var currentOffset = context.y
        var wrapper = context.pages.contentStreamFor(currentOffset, leading)
        var stream = wrapper.stream
        currentOffset = wrapper.virtualOffset
        var currentPage = wrapper.page
        var lastX = 0f
        prepareStream(
            stream,
            leading = leading,
            x = context.x,
            y = wrapper.realOffset,
            font = font,
            style = style
        )
        lines.forEachIndexed { i, line ->
            val oldWrapper = wrapper
            if (i > 0) {
                resetStreamSettings(stream)
                wrapper = context.pages.contentStreamFor(currentOffset, leading)
                stream = wrapper.stream
            }
            if (wrapper.page != currentPage) {
                currentPage = wrapper.page
                oldWrapper.stream.endText()
                lastX = 0f
                prepareStream(
                    stream,
                    leading = leading,
                    x = context.x,
                    y = wrapper.realOffset,
                    font = font,
                    style = style
                )
            }
            val lineWidth = textWidth(line, style.font, style.fontSize)
            // newLineAtOffset uses relative coordinates
            when (align) {
                TextAlign.LEFT -> {
                    stream.newLine()
                }

                TextAlign.JUSTIFIED -> {
                    stream.newLine()
                    if (i < lines.size - 1) {
                        val spaceCount = line.count { it == ' ' }
                        val spacing = (width - lineWidth) / spaceCount
                        stream.setWordSpacing(spacing)
                    }

                }

                TextAlign.RIGHT -> {
                    val x = width - lineWidth
                    stream.newLineAtOffset(x - lastX, -leading)
                    lastX = x
                }
            }
            stream.showText(line)
            currentOffset = wrapper.virtualOffset + leading
        }
        stream.endText()
        resetStreamSettings(stream)
        return Size(width = width, height = currentOffset - context.y)
    }

    private fun prepareStream(
        stream: PDPageContentStream,
        leading: Float,
        x: Float,
        y: Float,
        font: PDFont,
        style: TextStyle
    ) {
        stream.beginText()
        stream.setLeading(leading)
        stream.newLineAtOffset(x, y)
        stream.setFont(font, style.fontSize)
        stream.setNonStrokingColor(style.textColor)
    }

    private fun resetStreamSettings(stream: PDPageContentStream) {
        stream.setCharacterSpacing(0f)
    }

    private fun getMaxLineSize(font: PDFont, style: TextStyle) =
        lines.maxOf { textWidth(it, font, style.fontSize) }
}

enum class TextAlign {
    LEFT, JUSTIFIED, RIGHT
}
