package pdfcrate.components

import pdfcrate.exceptions.NotEnoughSizeException
import pdfcrate.render.ComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.Sizing
import kotlin.math.max

/**
 * Renders elements in a column-based layout
 *
 * If the columns are too big for this page, they will continue in the next, in the same layout
 *
 * This component always use the full width of the parent container
 *
 * @see [HorizontalLayoutColumn]
 */
class HorizontalLayout(private vararg val columns: HorizontalLayoutColumn) : Component {
    constructor(columns: List<HorizontalLayoutColumn>) : this(*columns.toTypedArray())

    override fun render(context: ComponentContext): Size {
        val availableSpace = context.maxX - context.x
        val sizes = getSizes(availableSpace)
        var offsetX = context.x
        var height = 0f
        for (i in sizes.indices) {
            val columnSize = sizes[i]
            val column = columns[i]
            val padding = column.padding
            val childContext = context.withLimits(
                offsetX + padding.left,
                offsetX + columnSize - padding.right,
                context.y + padding.top
            )
            val renderResult = column.child.render(childContext)
            height = max(renderResult.height + padding.bottom + padding.top, height)
            offsetX += columnSize
        }
        return Size(availableSpace, height)
    }

    private fun getSizes(availableSpace: Float): FloatArray {
        val sizes = FloatArray(columns.size)
        var totalAbsolute = 0f
        var totalProportional = 0f
        columns.forEachIndexed { index, constraints ->
            when (constraints.style) {
                Sizing.ABSOLUTE -> {
                    sizes[index] = constraints.size
                    totalAbsolute += constraints.size
                }

                Sizing.PROPORTIONAL -> totalProportional += constraints.size
                else -> throw NotImplementedError("HorizontalLayout does not support shrink style")
            }
        }
        val totalRemaining = availableSpace - totalAbsolute
        if (totalRemaining < 0) {
            throw NotEnoughSizeException("Not enough space to render absolute-sized columns")
        }

        columns.forEachIndexed { index, constraints ->
            if (constraints.style == Sizing.PROPORTIONAL) {
                sizes[index] = totalRemaining * constraints.size / totalProportional
            }
        }
        return sizes
    }
}

/**
 * Defines a column for [HorizontalLayout]
 *
 * The column has the child component, its size and, optionally, a padding
 *
 * @see [Sizing]
 */
class HorizontalLayoutColumn private constructor(
    val child: Component,
    val style: Sizing,
    val size: Float = 0f,
    val padding: Edges = Edges.ZERO
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun absolute(child: Component, size: Float, padding: Edges = Edges.ZERO) =
            HorizontalLayoutColumn(child, Sizing.ABSOLUTE, size, padding)

        @JvmStatic
        @JvmOverloads
        fun proportional(child: Component, size: Float, padding: Edges = Edges.ZERO) =
            HorizontalLayoutColumn(child, Sizing.PROPORTIONAL, size, padding)
    }
}
