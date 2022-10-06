package pdfcrate.util

import org.apache.pdfbox.pdmodel.font.PDFont


class RenderingUtil {
    companion object {
        @JvmStatic
        val WHITESPACE_REGEX = Regex("\\s")

        @JvmStatic
        fun textWidth(text: String, font: PDFont, fontSize: Float): Float {
            return font.getStringWidth(text) * fontSize / 1000
        }
    }
}

