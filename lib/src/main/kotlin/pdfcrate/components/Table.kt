package pdfcrate.components

import pdfcrate.render.ComponentContext
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.Sizing

class Table(private val data: Array<Array<SizedComponent>>, private val columns: Array<TableColumn>) : Component {
    override fun render(context: ComponentContext): Size {
        val columnsSize = getWidths(context)
        var heightUsed = 0f
        for (row in data) {
            val maxHeight = row.withIndex().maxOf { (i, component) ->
                val padding = columns[i].padding
                component.getBlocks(context).totalHeight() + ((padding?.top ?: 0f) + (padding?.bottom ?: 0f))
            }
            val wrapper = context.pages.contentStreamFor(context.y + heightUsed, maxHeight)
            val rowY = wrapper.virtualOffset
            var x = context.x
            for ((i, cell) in row.withIndex()) {
                val width = columnsSize[i]
                val padding = columns[i].padding
                val limits = context.withLimits(x = x, maxX = width + x, y = rowY)
                if (padding != null) {
                    Padding(cell, padding).render(limits)
                } else {
                    cell.render(limits)
                }
                x += width
            }
            heightUsed = rowY + maxHeight - context.y
        }
        return Size(context.width(), heightUsed)
    }

    private fun getWidths(context: ComponentContext): FloatArray {
        val widths = FloatArray(columns.size)
        var absoluteSize = 0f
        var proportionalRatioBase = 0f
        for ((i, column) in columns.withIndex()) {
            when (column.sizing.style) {
                Sizing.SHRINK -> {
                    val width = getShrinkWidth(context, i) + (column.padding?.horizontalSize() ?: 0f)
                    widths[i] = width
                    absoluteSize += width
                }

                Sizing.ABSOLUTE -> {
                    val width = column.sizing.value + (column.padding?.horizontalSize() ?: 0f)
                    widths[i] = width
                    absoluteSize += width
                }

                Sizing.PROPORTIONAL -> proportionalRatioBase += column.sizing.value
            }
        }
        val remaining = context.width() - absoluteSize
        columns.withIndex()
            .filter { it.value.sizing.style == Sizing.PROPORTIONAL }
            .forEach {
                widths[it.index] = it.value.sizing.value / proportionalRatioBase * remaining
            }
        return widths
    }

    private fun getShrinkWidth(context: ComponentContext, i: Int): Float {
        return data.maxOf { row -> row[i].getBlocks(context).width }
    }
}

class TableColumn private constructor(val sizing: ColumnSizing, val padding: Edges? = null) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun proportional(size: Float, padding: Edges? = null) =
            TableColumn(ColumnSizing(Sizing.PROPORTIONAL, size), padding)

        @JvmStatic
        @JvmOverloads
        fun absolute(size: Float, padding: Edges? = null) =
            TableColumn(ColumnSizing(Sizing.ABSOLUTE, size), padding)

        @JvmStatic
        @JvmOverloads
        fun shrink(padding: Edges? = null) =
            TableColumn(ColumnSizing(Sizing.SHRINK, 0f), padding)

    }
}

data class ColumnSizing(val style: Sizing, val value: Float)