package pdfcrate.components

import pdfcrate.document.LineStyle
import pdfcrate.document.Style
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Point
import pdfcrate.util.Size


class Lines(
    private val points: List<Point>,
    private val lineStyle: LineStyle? = null
) : Component {

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

    override fun render(style: Style, renderer: ContentBuilder): Size {
        return renderer.drawPath(
            points, lineStyle ?: style.lineStyle
        )
    }
}
