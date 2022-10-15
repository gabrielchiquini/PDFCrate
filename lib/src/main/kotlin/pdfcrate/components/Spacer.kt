package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

class Spacer(private val width: Float = 0f, private val height: Float = 0f) : SizedComponent {
    override fun getBlocks(context: ComponentContext): SizeBlocks {
        return SizeBlocks.single(width, height)
    }

    override fun render(context: ComponentContext): Size {
        return Size(width, height)
    }
}
