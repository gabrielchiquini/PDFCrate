package pdfcrate.util

import org.apache.pdfbox.pdmodel.common.PDRectangle
import kotlin.math.max
import kotlin.math.min

data class Size(val width: Float, val height: Float) {

    companion object {
        @JvmStatic
        fun fromPoints(point1: Point, point2: Point): Size {
            val minX = min(point1.x, point2.x)
            val maxX = max(point1.x, point2.x)
            val minY = min(point1.y, point2.y)
            val maxY = max(point1.y, point2.y)
            return Size(
                width = maxX - minX,
                height = maxY - minY
            )
        }

        @JvmStatic
        val ZERO = Size(0f, 0f)

        @JvmStatic
        val A4 = Size(PDRectangle.A4.width, PDRectangle.A4.height)
    }

}