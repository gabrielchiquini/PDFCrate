package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.SizeBlocks

/**
 * An element which determines its size before rendering
 */
interface SizedComponent : Component {
    /**
     * Calculates the size of this component
     *
     * This methods must NOT write anything to the document and
     * must be idempotent, because it can be called multiple times
     *
     * It should not rely on the y position on the context, because it can be different in the [Component.render] call
     *
     * It should disconsider any possible blank space in page breaks, the calling component must allocate the correct amount of height
     * for this size be consistent with [Component.render]
     */
    fun getBlocks(context: ComponentContext): SizeBlocks
}
