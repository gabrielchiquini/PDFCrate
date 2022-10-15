package pdfcrate.components

import pdfcrate.exceptions.NotEnoughSizeException
import pdfcrate.render.ComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.Sizing
import kotlin.math.max

class HorizontalLayout(private val columns: List<HorizontalLayoutColumn>) : Component {

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

class HorizontalLayoutColumn constructor(
    val child: Component,
    val style: Sizing,
    val size: Float = 0f,
    val padding: Edges = Edges.ZERO
) {
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
    constructor(builder: Builder) : this(
        child = builder.child!!,
        style = builder.style!!,
        size = builder.size,
        padding = builder.padding ?: Edges.ZERO,
    )

    class Builder {
        var child: Component? = null
        var style: Sizing? = null
        var size: Float = 0f
        var padding: Edges? = null

        fun child(value: Component) = apply { this.child = value }
        fun style(value: Sizing) = apply { this.style = value }
        fun size(value: Float) = apply { this.size = value }
        fun padding(value: Edges) = apply { this.padding = value }
        fun build() = HorizontalLayoutColumn(this)
    }
}
