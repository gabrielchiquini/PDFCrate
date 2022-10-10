package pdfcrate.util

class Edges(val top: Float, val right: Float, val bottom: Float, val left: Float) {
    companion object {
        @JvmStatic
        val ZERO = all(0f)

        @JvmStatic
        fun symmetric(horizontal: Float, vertical: Float) = Edges(vertical, horizontal, vertical, horizontal)

        @JvmStatic
        fun all(value: Float) = Edges(value, value, value, value)
    }

    fun horizontalSize(): Float {
        return left + right
    }
}