package pdfcrate.document

import java.awt.Color

class LineStyle(
    val dashPattern: FloatArray,
    val dashPhase: Float,
    val capStyle: Int,
    val width: Float,
    val color: Color,
) {

    companion object {
        @JvmStatic
        val DEFAULT_STYLE = LineStyle(
            dashPattern = floatArrayOf(),
            dashPhase = 0f,
            capStyle = 0,
            width = 1f,
            color = Color.BLACK,
        )

        @JvmStatic
        fun builder() = Builder()
    }

    constructor(
        dashPattern: FloatArray? = null,
        dashPhase: Float? = null,
        capStyle: Int? = null,
        width: Float? = null,
        color: Color? = null,
    ) : this(
        dashPattern = dashPattern ?: floatArrayOf(),
        dashPhase = dashPhase ?: 0f,
        capStyle = capStyle ?: 0,
        width = width ?: 1f,
        color = color ?: Color.BLACK,
    )

    constructor(builder: Builder) : this(
        dashPattern = builder.dashPattern,
        dashPhase = builder.dashPhase,
        capStyle = builder.capStyle,
        width = builder.width,
        color = builder.color,
    )

    class Builder {
        var dashPattern: FloatArray? = null
        var dashPhase: Float? = null
        var capStyle: Int? = null
        var width: Float? = null
        var color: Color? = null

        fun dashPattern(value: FloatArray) = apply { this.dashPattern = value }
        fun dashPhase(value: Float) = apply { this.dashPhase = value }
        fun capStyle(value: Int) = apply { this.capStyle = value }
        fun width(value: Float) = apply { this.width = value }
        fun color(value: Color) = apply { this.color = value }
        fun build() = LineStyle(this)
    }
}
