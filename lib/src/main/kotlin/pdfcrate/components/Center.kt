package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import kotlin.math.max

/**
 * Center a child element in the parent container
 */
class Center(private val child: SizedComponent) : SizedComponent {
    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val blocks = child.getBlocks(context)
        return SizeBlocks(max(context.width(), blocks.width), blocks.heightBlocks)
    }

    override fun render(context: ComponentContext): Size {
        val size = child.getBlocks(context)
        val x = max((context.width() - size.width) / 2, 0f)
        val componentContext = context.withLimits(x, x + size.width, context.y)
        val (width, height) = child.render(componentContext)
        return Size(max(width, context.width()), height)
    }

}
