package pdfcrate.components

import pdfcrate.document.Style
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Edges
import pdfcrate.util.Size

class Padding(private val inner: Component, private val padding: Edges) : Component {

    override fun render(style: Style, renderer: ContentBuilder): Size {
        val rendered = inner.render(
            style,
            renderer.withLimits(
                x = renderer.x + padding.left,
                maxX = renderer.maxX - padding.right,
                startingY = renderer.startingY + padding.top
            )
        )
        return rendered.copy(
            width = rendered.width + padding.left + padding.right,
            height = rendered.height + padding.top + padding.bottom
        )
    }
}