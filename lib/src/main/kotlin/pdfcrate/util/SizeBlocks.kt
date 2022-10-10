package pdfcrate.util

class SizeBlocks(val width: Float, val heightBlocks: List<Float>) {
    companion object {
        @JvmStatic
        fun single(width: Float, height: Float): SizeBlocks = SizeBlocks(width, listOf(height))
    }

    fun totalHeight() = heightBlocks.sum()

}