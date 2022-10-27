package pdfcrate.document

import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.awt.Color

/**
 * Defines the text style to be used in the document or in a single component
 * @property font The font to be used, must be added to the document with [pdfcrate.document.Document.loadFont]
 * @property fontSize The font size in pixels (default: 13)
 * @property textColor The text color (default: black)
 * @property leading The line height relative to the font size. Line height is calculated as `leading * fontSize` (default: 1)
 */
class TextStyle(
    val font: PDFont,
    val fontSize: Float,
    val textColor: Color,
    val leading: Float
) {
    constructor(
        font: PDFont? = null, fontSize: Float? = null, textColor: Color? = null, leading: Float? = null,
    ) : this(
        font = font ?: default().font,
        fontSize = fontSize ?: default().fontSize,
        textColor = textColor ?: default().textColor,
        leading = leading ?: default().leading
    )

    constructor(builder: Builder) : this(
        font = builder.font,
        fontSize = builder.fontSize,
        textColor = builder.textColor,
        leading = builder.leading,
    )

    class Builder(
        var font: PDFont? = null,
        var fontSize: Float? = null,
        var textColor: Color? = null,
        var leading: Float? = null,
    ) {
        fun font(value: PDFont?) = apply { this.font = value }
        fun fontSize(value: Float?) = apply { this.fontSize = value }
        fun textColor(value: Color?) = apply { this.textColor = value }
        fun leading(value: Float) = apply { this.leading = value }
        fun build() = TextStyle(this)

    }

    /**
     * Returns a [Builder] instance with current style
     */
    fun toBuilder(): Builder {
        return Builder(
            font = font, fontSize = fontSize, textColor = textColor, leading = leading,
        )
    }

    companion object {
        @JvmStatic
        fun default() =
            TextStyle(font = PDType1Font.HELVETICA, fontSize = 13f, textColor = Color.BLACK, leading = 1f)

        @JvmStatic
        fun builder() = Builder()
    }

}
