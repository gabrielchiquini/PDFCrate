package pdfcrate.document

import pdfcrate.components.Component
import pdfcrate.render.Renderer
import pdfcrate.render.RendererContext
import pdfcrate.util.Size
import java.io.OutputStream

class Document {
    private var style: Style = Style.DEFAULT_STYLE
    var margin = Size.ZERO
    var size = Size.A4
    private val components = mutableListOf<Component>()

    fun add(component: Component) {
        components.add(component)
    }

    fun render(outputStream: OutputStream) {
        val renderer = Renderer(
            RendererContext(
                style = style,
                pageSize = size,
                margin = margin,
            ), components
        )
        renderer.render(outputStream)
    }
}