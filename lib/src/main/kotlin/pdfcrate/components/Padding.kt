package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.SizeBlocks
import pdfcrate.util.Edges
import pdfcrate.util.Size

class Padding(private val inner: SizedComponent, private val padding: Edges) : SizedComponent {
    private val delegate = UnsizedPadding(inner, padding)
    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val innerBlocks = inner.getBlocks(
            context.withLimits(
                x = context.x + padding.left,
                maxX = context.maxX - padding.right,
                y = context.y + padding.top,
            )
        )
        val heights = ArrayList<Float>(innerBlocks.heightBlocks.size + 2)
        heights.add(padding.top)
        heights.addAll(innerBlocks.heightBlocks)
        heights.add(padding.bottom)
        return SizeBlocks(innerBlocks.width + padding.left + padding.right, heights)
    }

    override fun render(context: ComponentContext): Size {
        return delegate.render(context)
    }
}

class UnsizedPadding(private val inner: Component, private val padding: Edges) : Component {
    override fun render(context: ComponentContext): Size {
        val rendered = inner.render(
            context.withLimits(
                x = context.x + padding.left,
                maxX = context.maxX - padding.right,
                y = context.y + padding.top,
            )
        )
        return rendered.copy(
            width = rendered.width + padding.left + padding.right,
            height = rendered.height + padding.top + padding.bottom
        )
    }
}
