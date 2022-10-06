package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Size

interface Component {
    fun render(context: ComponentContext): Size
}
