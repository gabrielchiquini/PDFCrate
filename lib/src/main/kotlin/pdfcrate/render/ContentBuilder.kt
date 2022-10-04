package pdfcrate.render

import org.apache.pdfbox.pdmodel.font.PDFont
import pdfcrate.document.LineStyle
import pdfcrate.document.TextStyle
import pdfcrate.util.Point
import pdfcrate.util.Size
import kotlin.math.max
import kotlin.math.min


class ContentBuilder(
    private val pages: PageStream,
    val x: Float,
    val maxX: Float,
    val startingY: Float,
) {
    companion object {
        @JvmStatic
        private val WHITESPACE = Regex("\\s")
    }

    fun withLimits(x: Float, maxX: Float, startingY: Float) = ContentBuilder(pages, x, maxX, startingY)

    fun drawPath(points: List<Point>, lineParameters: LineStyle): Size {
        val (width, height) = pointsSize(points)
        val wrapper = pages.contentStreamFor(startingY, height)
        wrapper.stream.setLineCapStyle(lineParameters.capStyle)
        wrapper.stream.setLineWidth(lineParameters.width)
        wrapper.stream.setLineDashPattern(lineParameters.dashPattern, lineParameters.dashPhase)
        wrapper.stream.setStrokingColor(lineParameters.color)
        val iterator = points.listIterator()
        val first = iterator.next()
        val start = translatePointToOffset(first, x, wrapper.realOffset)
        wrapper.stream.moveTo(start.x, start.y)
        iterator.forEachRemaining {
            val (x, y) = translatePointToOffset(it, x, wrapper.realOffset)
            wrapper.stream.lineTo(x, y)
            wrapper.stream.moveTo(x, y)
        }
        wrapper.stream.stroke()
        return Size(width, height)
    }

    private fun pointsSize(points: List<Point>): Size {
        var minX = 0f
        var maxX = 0f
        var minY = 0f
        var maxY = 0f
        points.forEach {
            minX = min(minX, it.x)
            maxX = max(maxX, it.x)
            minY = min(minY, it.y)
            maxY = max(maxY, it.y)
        }
        return Size(maxX - minX, maxY - minY)
    }

    fun writeTextLines(text: String, style: TextStyle): Size {
        val lines = text.split('\n')
        return writeTextLinesInternal(lines, style)
    }

    fun writeFlexSizeText(text: String, style: TextStyle, align: Alignment): Size {
        val font = style.font
        val width = maxX - x
        val maxSize = width * 1000 / font.getStringWidth(text)
        val size = min(style.fontSize, maxSize)
        val leading = style.leading * size
        val wrapper = pages.contentStreamFor(startingY, leading)
        wrapper.stream.beginText()
        val y = wrapper.realOffset - leading
        val textWidth = style.font.getStringWidth(text) * size / 1000
        val (newX, usedWidth) = when (align) {
            Alignment.LEFT -> {
                Pair(x, getStringWidth(text, font, size))
            }

            Alignment.CENTER -> {
                Pair((width - textWidth) / 2, maxX - x)
            }
        }
        wrapper.stream.newLineAtOffset(newX, y)
        wrapper.stream.setFont(font, size)
        wrapper.stream.showText(text)
        wrapper.stream.endText()
        return Size(width = usedWidth, height = wrapper.virtualOffset + leading - this.startingY)
    }

    fun writeWrappingText(text: String, style: TextStyle): Size {
        val words = text.split(WHITESPACE)
        val lineSize = maxX - x
        val builder = StringBuilder()
        val lines = mutableListOf<String>()
        words.forEach {
            if (builder.isNotEmpty()) {
                builder.append(' ')
            }
            builder.append(it)
            val line = builder.toString()
            if (getStringWidth(line, style.font, style.fontSize) > lineSize) {
                builder.delete(builder.length - it.length - 1, builder.length)
                lines.add(builder.toString())
                builder.setLength(0)
                builder.append(it)
            }
        }
        if (builder.isNotEmpty()) {
            lines.add(builder.toString())
        }
        return writeTextLinesInternal(lines, style)
    }

    private fun translatePointToOffset(point: Point, x: Float, y: Float): Point {
        return Point(point.x + x, y - point.y)
    }

    private fun writeTextLinesInternal(
        lines: List<String>,
        style: TextStyle
    ): Size {
        val font = style.font
        val leading = style.leading * style.fontSize
        var width = 0f
        var currentOffset = startingY
        var wrapper = pages.contentStreamFor(currentOffset, leading)
        currentOffset = wrapper.virtualOffset
        var currentPage = wrapper.page
        wrapper.stream.beginText()
        wrapper.stream.setLeading(leading)
        val y = wrapper.realOffset - leading
        wrapper.stream.newLineAtOffset(x, y)
        lines.forEachIndexed { i, line ->
            val oldWrapper = wrapper
            if (i > 0) {
                wrapper = pages.contentStreamFor(currentOffset, leading)
            }
            if (wrapper.page != currentPage) {
                currentPage = wrapper.page
                oldWrapper.stream.endText()
                wrapper.stream.beginText()
                wrapper.stream.newLineAtOffset(x, wrapper.realOffset - leading)
                wrapper.stream.setLeading(leading)
            }
            wrapper.stream.setFont(font, style.fontSize)
            wrapper.stream.showText(line)
            width = max(width, getStringWidth(line, style.font, style.fontSize))
            wrapper.stream.newLine()
            currentOffset = wrapper.virtualOffset + leading
        }
        wrapper.stream.endText()

        return Size(width = width, height = currentOffset - startingY)
    }

    private fun getStringWidth(text: String, font: PDFont, fontSize: Float): Float {
        return font.getStringWidth(text) * fontSize / 1000
    }
}
