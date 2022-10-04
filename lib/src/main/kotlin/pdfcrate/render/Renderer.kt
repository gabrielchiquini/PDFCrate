package pdfcrate.render

import org.apache.pdfbox.pdmodel.PDDocument
import pdfcrate.components.Component
import java.io.OutputStream

class Renderer(private val context: RendererContext, private val components: MutableList<Component>) {

    internal fun render(outputStream: OutputStream) {
        val document = PDDocument()
        val pages = PageStream(document, context)
        var virtualY = 0f
        components.forEach { component ->
            val content = ContentBuilder(
                pages = pages,
                x = context.margin.left,
                maxX = context.pageSize.width - context.margin.right,
                startingY = virtualY,
            )
            val box = component.render(context.style, content)
            virtualY += box.height
        }
        pages.close()
        document.save(outputStream)
    }
}

