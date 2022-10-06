package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size

class Padding(private val inner: Component, private val padding: Edges) : Component {

    override fun render(context: ComponentContext): Size {
        val rendered = inner.render(
            context.withLimits(
                x = context.x + padding.left,
                maxX = context.maxX - padding.right,
                startingY = context.y + padding.top,
            )
        )
        return rendered.copy(
            width = rendered.width + padding.left + padding.right,
            height = rendered.height + padding.top + padding.bottom
        )
    }
}