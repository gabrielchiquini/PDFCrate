package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import kotlin.math.max

/**
 * Wraps multiple elements horizontally in the parent container
 *
 * It will always render the entire row in the same page, even if some child has multiple blocks
 */
class Row(private vararg val children: SizedComponent) : SizedComponent {

    constructor(columns: List<SizedComponent>) : this(*columns.toTypedArray())

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        var width = 0f
        var height = 0f
        children.forEach {
            val blocks = it.getBlocks(context.withLimits(x = context.x + width))
            width += blocks.width
            height = max(height, blocks.totalHeight())
        }
        return SizeBlocks.single(width, height)
    }

    override fun render(context: ComponentContext): Size {
        var x = context.x
        var maxHeight = 0f
        for (component in children) {
            val (width, height) = component.render(context.withLimits(x, context.maxX, context.y))
            x += width
            maxHeight = max(maxHeight, height)
        }
        return Size(x, maxHeight)
    }
}
