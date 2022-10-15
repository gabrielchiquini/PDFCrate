package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size

class Footer(private val child: SizedComponent) : Component {
    override fun render(context: ComponentContext): Size {
        val blocks = child.getBlocks(context)
        val height = blocks.totalHeight()
        val wrapper = context.pages.contentStreamFor(context.y, height)
        val realOffset = wrapper.realOffset
        val span = realOffset - height - context.renderContext.margin.bottom
        return Padding(child, Edges(top = span)).render(context)
    }
}
