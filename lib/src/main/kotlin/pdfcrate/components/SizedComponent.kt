package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.SizeBlocks

interface SizedComponent : Component {
    fun getBlocks(context: ComponentContext): SizeBlocks
}
