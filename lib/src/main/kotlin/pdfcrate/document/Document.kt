package pdfcrate.document

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType0Font
import pdfcrate.components.Component
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.render.RenderContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

class Document {
    private var style: Style = Style.DEFAULT_STYLE
    var margin = Edges.all(0f)
        private set
    var size = Size.A4
        private set
    private val components = mutableListOf<Component>()

    fun add(component: Component) = apply { components.add(component) }
    fun addAll(components: List<Component>) = apply { this.components.addAll(components) }

    fun textStyle(style: TextStyle) = apply { this.style.textStyle = style }

    fun lineStyle(style: LineStyle) = apply { this.style.lineStyle = style }
    fun size(size: Size) = apply { this.size = size }
    fun margin(margin: Edges) = apply { this.margin = margin }

    fun textStyle() = style.textStyle

    fun lineStyle() = style.lineStyle

    fun loadFont(fontFile: File): PDType0Font {
        return PDType0Font.load(pdDocument, FileInputStream(fontFile), false)
    }

    fun getContentSize() = Size(size.width - margin.horizontalSize(), size.height - margin.verticalSize())

    private val pdDocument = PDDocument()

    fun render(outputStream: OutputStream) {
        val context = RenderContext(
            style = style,
            pageSize = size,
            margin = margin,
        )
        val document = pdDocument
        val pages = PageStream(document, context)
        var virtualY = 0f
        components.forEach { component ->
            val box = component.render(
                ComponentContext(
                    renderContext = context,
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
