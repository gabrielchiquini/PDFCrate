package pdfcrate.util

import org.apache.pdfbox.pdmodel.common.PDRectangle

data class Size(val width: Float, val height: Float) {

    companion object {
        @JvmStatic
        val ZERO = Size(0f, 0f)

        @JvmStatic
        val A4 = Size(PDRectangle.A4.width, PDRectangle.A4.height)
    }

}