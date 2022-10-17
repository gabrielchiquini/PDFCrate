package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Size

/**
 * An element to be added in the document
 */
interface Component {
    /**
     * Renders the component in the PDF document
     *
     * Will be called once and does not need to provide idempotency
     * @param context provides access to PDFBox streams to add content to the document
     *                and the current position on the document
     * @return The size this element occupied in the document.
     *         If this element caused a page break, it must return the entire height taken,
     *         the blank space in previous page plus the space in the page where it was rendered
      */
    fun render(context: ComponentContext): Size
}
