package pdfcrate.document

import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.awt.Color

class TextStyle(
    val font: PDFont, val fontSize: Float, val textColor: Color, val leading: Float
) {

    constructor(
        font: PDFont? = null, fontSize: Float? = null, textColor: Color? = null, leading: Float? = null,
    ) : this(
        font = font ?: DEFAULT_STYLE.font,
        fontSize = fontSize ?: DEFAULT_STYLE.fontSize,
        textColor = textColor ?: DEFAULT_STYLE.textColor,
        leading = leading ?: DEFAULT_STYLE.leading
    )

    constructor(builder: Builder) : this(
        font = builder.font,
        fontSize = builder.fontSize,
        textColor = builder.textColor,
        leading = builder.leading,
    )

    class Builder {
        internal var font: PDFont? = null
        internal var fontSize: Float? = null
        internal var textColor: Color? = null
        internal var leading: Float? = null

        fun font(value: PDFont?) = apply { this.font = value }
        fun fontSize(value: Float?) = apply { this.fontSize = value }
        fun textColor(value: Color?) = apply { this.textColor = value }
        fun leading(value: Float) = apply { this.leading = value }
        fun build() = TextStyle(this)

    }

    companion object {
        @JvmStatic
        val DEFAULT_STYLE =
            TextStyle(font = PDType1Font.HELVETICA, fontSize = 13f, textColor = Color.BLACK, leading = 1f)

        @JvmStatic
        fun builder() = Builder()

    }

}