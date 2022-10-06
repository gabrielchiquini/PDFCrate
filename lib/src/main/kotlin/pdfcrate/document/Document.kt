package pdfcrate.document

import org.apache.pdfbox.pdmodel.PDDocument
import pdfcrate.components.Component
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.render.RenderContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import java.io.OutputStream

class Document {
    private var style: Style = Style.DEFAULT_STYLE
    var margin = Edges.all(0f)
    var size = Size.A4
    private val components = mutableListOf<Component>()

    fun add(component: Component) {
        components.add(component)
    }

    fun render(outputStream: OutputStream) {
        val context = RenderContext(
            style = style,
            pageSize = size,
            margin = margin,
        )
        val document = PDDocument()
        val pages = PageStream(document, context)
        var virtualY = 0f
        components.forEach { component ->
            val box = component.render(
                ComponentContext(
                    pages = pages,
                    x = context.margin.left,
                    maxX = context.pageSize.width - context.margin.right,
                    y = virtualY,
                    style = context.style,
                )
            )
            virtualY += box.height
        }
        pages.close()
        document.save(outputStream)
    }

}
