package pdfcrate.render

import pdfcrate.document.Style

class ComponentContext(
    val pages: PageStream,
    val renderContext: RenderContext,
    val x: Float,
    val maxX: Float,
    val y: Float,
    val style: Style,
) {
    fun withLimits(x: Float = this.x, maxX: Float = this.maxX, y: Float = this.y) = ComponentContext(
        renderContext = renderContext,
        pages = pages,
        x = x,
        maxX = maxX,
        y = y,
        style = style
    )

    fun width(): Float = maxX - x
}
