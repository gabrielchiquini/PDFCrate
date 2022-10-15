package pdfcrate.util

class Edges(val top: Float = 0f, val right: Float = 0f, val bottom: Float = 0f, val left: Float = 0f) {
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

    fun verticalSize(): Float {
        return top + bottom
    }
}