package pdfcrate.document

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding
import pdfcrate.components.Component
import pdfcrate.render.ComponentContext
import pdfcrate.render.PageStream
import pdfcrate.render.RenderContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * A container for all components to be added in and to be rendered to a PDF file
 *
 * The components are rendered in the order they were added
 */
class Document {
    private var style: Style = Style.default()
    var margin = Edges.all(0f)
        private set
    var size = Size.A4
        private set
    private val components = mutableListOf<Component>()

    /**
     * Adds a new component to the document
     * @return a reference to this Document
     */
    fun add(component: Component) = apply { components.add(component) }

    /**
     * Adds a list of components to the document
     * @return a reference to this Document
     */
    fun addAll(components: List<Component>) = apply { this.components.addAll(components) }

    /**
     * Sets the default text style for this document
     * @return a reference to this Document
     */
    fun textStyle(style: TextStyle) = apply { this.style.textStyle = style }

    /**
     * Sets the default line style for this document
     * @return a reference to this Document
     */
    fun lineStyle(style: LineStyle) = apply { this.style.lineStyle = style }

    /**
     * Sets the page size, this applies to all pages
     *
     * Different sizes for each page are not supported
     * @return a reference to this Document
     */
    fun size(size: Size) = apply { this.size = size }

    /**
     * Sets the page margin, this applies to all pages
     * @return a reference to this Document
     */
    fun margin(margin: Edges) = apply { this.margin = margin }

    fun textStyle() = style.textStyle

    fun lineStyle() = style.lineStyle

    /**
     * Loads a font from a file and adds it to the document.
     *
     * The font is added as a [PDType0Font] and some features may not be available.
     * Use [loadTTF] for full feature support
     *
     * @return the font object
     */
    fun loadFont(fontFile: File): PDFont {
        return loadFont(FileInputStream(fontFile))
    }

    /**
     * Loads a font from an input stream and adds it to the document.
     * The font is added as a [PDType0Font].
     * @return the font object
     */
    fun loadFont(stream: InputStream): PDFont {
        return PDType0Font.load(pdDocument, stream)
    }

    /**
     * Loads a TTF font from a file and adds it to the document.
     * The font is added as a [PDTrueTypeFont]. For Unicode support use [loadFont]
     * @return the font object
     */
    fun loadTTF(fontFile: File): PDFont {
        return loadTTF(FileInputStream(fontFile))
    }

    /**
     * Loads a TTF font from a file and adds it to the document.
     * The font is added as a [PDTrueTypeFont]. For Unicode support use [loadFont]
     * @return the font object
     */
    fun loadTTF(stream: InputStream): PDFont {
        return PDTrueTypeFont.load(pdDocument, stream, WinAnsiEncoding.INSTANCE)
    }

    /**
     * Returns the page size minus margins in each side
     */
    fun getContentSize() = Size(size.width - margin.horizontalSize(), size.height - margin.verticalSize())

    private val pdDocument = PDDocument()

    /**
     * Renders this document using the compoents previously added
     *
     * This function must be called once for the object, because the [PDDocument] instance is kept
     *
     * The document is closed in the end
     */
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
        outputStream.close()
        document.close()
    }
}
