package pdfcrate.components

import pdfcrate.document.Style
import pdfcrate.exceptions.NotEnoughSizeException
import pdfcrate.render.ContentBuilder
import pdfcrate.util.Edges
import pdfcrate.util.Size
import pdfcrate.util.SpacingStyle
import kotlin.math.max

class ColumnLayout(content: List<Component>, constraints: List<ColumnConstraints>) : Component {
    private var constraints: List<ColumnConstraints>
    private var content: List<Component>

    init {
        if (content.size != constraints.size) {
            throw IllegalArgumentException("Content and constraints must have the same size")
        }
        if (content.isEmpty()) {
            throw IllegalArgumentException("Must have at least one column")
        }
        this.content = content
        this.constraints = constraints
    }

    override fun render(style: Style, renderer: ContentBuilder): Size {
        val availableSpace = renderer.maxX - renderer.x
        val sizes = getSizes(availableSpace)
        var offsetX = renderer.x
        var height = 0f
        for (i in sizes.indices) {
            val columnSize = sizes[i]
            val padding = constraints[i].padding
            val builder = renderer.withLimits(
                offsetX + padding.left,
                offsetX + columnSize - padding.right,
                renderer.startingY + padding.top
            )
            val renderResult = content[i].render(style, builder)
            height = max(renderResult.height + padding.bottom, height)
            offsetX += columnSize
        }
        return Size(availableSpace, height)
    }

    private fun getSizes(availableSpace: Float): FloatArray {
        val sizes = FloatArray(content.size)
        var totalAbsolute = 0f
        var totalProportional = 0f
        constraints.forEachIndexed { index, constraints ->
            when (constraints.style) {
                SpacingStyle.ABSOLUTE -> {
                    sizes[index] = constraints.value
                    totalAbsolute += constraints.value
                }

                SpacingStyle.PROPORTIONAL -> totalProportional += constraints.value
            }
        }
        val totalRemaining = availableSpace - totalAbsolute
        if (totalRemaining < 0) {
            throw NotEnoughSizeException("Not enough space to render absolute-sized columns")
        }

        constraints.forEachIndexed { index, constraints ->
            if (constraints.style == SpacingStyle.PROPORTIONAL) {
                sizes[index] = totalRemaining * constraints.value / totalProportional
            }
        }
        return sizes
    }
}

class ColumnConstraints(
    internal val style: SpacingStyle,
    internal val value: Float,
    internal val padding: Edges = Edges.all(0f)
)