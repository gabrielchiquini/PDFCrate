package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import kotlin.math.max

/**
 * Wraps multiple elements vertically in the parent container
 */
class Column(private vararg val children: SizedComponent) : SizedComponent {

    constructor(columns: List<SizedComponent>) : this(*columns.toTypedArray())

    private val delegate = UnsizedColumn(*children)

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        var width = 0f
        val totalBlocks = mutableListOf<Float>()
        var y = context.y
        children.forEach {
            val blocks = it.getBlocks(context.withLimits(y = y))
            width = max(width, blocks.width)
            totalBlocks.addAll(blocks.heightBlocks)
            y += blocks.totalHeight()
        }
        return SizeBlocks(width, totalBlocks)
    }

    override fun render(context: ComponentContext): Size {
        return delegate.render(context)
    }
}

/**
 * Unsized version of [Column]
 */
class UnsizedColumn(private vararg val children: Component) : Component {
    constructor(columns: List<Component>) : this(*columns.toTypedArray())

    override fun render(context: ComponentContext): Size {
        var y = context.y
        var maxWidth = 0f
        for (component in children) {
            val (width, height) = component.render(context.withLimits(context.x, context.maxX, y))
            y += height
            maxWidth = max(maxWidth, width)
        }
        return Size(maxWidth, y - context.y)
    }

}
