package pdfcrate.components

import pdfcrate.document.LineStyle
import pdfcrate.document.Style
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Point
import pdfcrate.util.Size


class Lines(
    private val start: Point,
    private val end: Point,
    private val lineStyle: LineStyle? = null
) : Component {

    constructor(builder: Builder) : this(
        start = builder.start ?: throw NullPointerException(),
        end = builder.end ?: throw NullPointerException(),
        lineStyle = builder.lineStyle,
    )

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder() {
        var start: Point? = null
        var end: Point? = null
        var lineStyle: LineStyle? = null

        fun start(value: Point) = apply { this.start = value }
        fun end(value: Point) = apply { this.end = value }
        fun lineStyle(value: LineStyle) = apply { this.lineStyle = value }
        fun build() = Lines(this)
    }

    override fun render(style: Style, renderer: ContentBuilder): Size {
        return renderer.drawLine(
            start, end, lineStyle ?: style.lineStyle
        )
    }
}
