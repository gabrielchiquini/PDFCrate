package pdfcrate.components

import pdfcrate.document.LineStyle
import pdfcrate.render.ComponentContext
import pdfcrate.util.SizeBlocks
import pdfcrate.util.Point
import pdfcrate.util.Size
import kotlin.math.max


class Lines(
    private val points: List<Point>,
    private val lineStyle: LineStyle? = null
) : SizedComponent {

    constructor(builder: Builder) : this(
        points = builder.points ?: throw NullPointerException(),
        lineStyle = builder.lineStyle,
    )

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder() {
        var points: List<Point>? = null
        var lineStyle: LineStyle? = null

        fun points(value: List<Point>) = apply { this.points = value }
        fun lineStyle(value: LineStyle) = apply { this.lineStyle = value }
        fun build() = Lines(this)
    }

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val (width, height) = pointsSize(points)
        return SizeBlocks.single(width, height)
    }

    override fun render(context: ComponentContext): Size {
        val pages = context.pages
        val style = lineStyle ?: context.style.lineStyle
        val (width, height) = pointsSize(points)
        val wrapper = pages.contentStreamFor(context.y, height)
        wrapper.stream.setLineCapStyle(style.capStyle)
        wrapper.stream.setLineWidth(style.width)
        wrapper.stream.setLineDashPattern(style.dashPattern, style.dashPhase)
        wrapper.stream.setStrokingColor(style.color)
        val iterator = points.listIterator()
        val first = iterator.next()
        val start = translatePointToOffset(first, context.x, wrapper.realOffset)
        wrapper.stream.moveTo(start.x, start.y)
        iterator.forEachRemaining {
            val (x, y) = translatePointToOffset(it, context.x, wrapper.realOffset)
            wrapper.stream.lineTo(x, y)
            wrapper.stream.moveTo(x, y)
        }
        wrapper.stream.stroke()
        return Size(width, height)

    }

    private fun pointsSize(points: List<Point>): Size {
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        points.forEach {
            maxX = max(maxX, it.x)
            maxY = max(maxY, it.y)
        }
        return Size(maxX, maxY)
    }

    private fun translatePointToOffset(point: Point, x: Float, y: Float): Point {
        return Point(point.x + x, y - point.y)
    }
}
