package pdfcrate.testutil

import org.assertj.core.api.Assertions
import org.assertj.core.data.Offset
import pdfcrate.document.Style
import pdfcrate.render.ComponentContext
import pdfcrate.render.RenderContext
import pdfcrate.util.Edges
import pdfcrate.util.Size

fun verifyComponentContext(context: ComponentContext, x: Float, maxX: Float, y: Float) {
    val offset = Offset.offset(0.1f)
    Assertions.assertThat(context.x).isCloseTo(x, offset)
    Assertions.assertThat(context.maxX).isCloseTo(maxX, offset)
    Assertions.assertThat(context.y).isCloseTo(y, offset)
}

fun generateRenderContext(size: Float, margin: Edges = Edges.ZERO) = RenderContext(
    Style.DEFAULT_STYLE, margin, Size(
        size,
        size
    )
)