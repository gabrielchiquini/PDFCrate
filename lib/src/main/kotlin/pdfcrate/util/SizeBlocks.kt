package pdfcrate.util

/**
 *  Determines the space used in vertical blocks
 *
 *  A block is a section where it can't be split amongst pages in the document
 *
 *  @property width The maximum horizontal size amongst the blocks
 *  @property heightBlocks A list containing the height of each block
 */
class SizeBlocks(val width: Float, val heightBlocks: List<Float>) {
    companion object {
        /**
         * Creates a [SizeBlocks] instance with a single block with the given size
         */
        @JvmStatic
        fun single(width: Float, height: Float): SizeBlocks = SizeBlocks(width, listOf(height))
    }

    /**
     * Returns the sum of all blocks
     */
    fun totalHeight() = heightBlocks.sum()

}
