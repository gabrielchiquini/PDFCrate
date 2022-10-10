package pdfcrate.render

import pdfcrate.document.Style

class ComponentContext(
    val pages: PageStream,
    val x: Float,
    val maxX: Float,
    val y: Float,
    val style: Style,
) {
    fun withLimits(x: Float, maxX: Float, y: Float) = ComponentContext(
        pages = pages,
        x = x,
        maxX = maxX,
        y = y,
        style = style
    )

    fun width(): Float = maxX - x
}
