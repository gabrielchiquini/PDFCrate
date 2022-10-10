package pdfcrate.testutil

import org.assertj.core.api.Assertions
import org.assertj.core.data.Offset
import pdfcrate.render.ComponentContext

fun verifyComponentContext(context: ComponentContext, x: Float, maxX: Float, y: Float) {
    val offset = Offset.offset(0.1f)
    Assertions.assertThat(context.x).isCloseTo(x, offset)
    Assertions.assertThat(context.maxX).isCloseTo(maxX, offset)
    Assertions.assertThat(context.y).isCloseTo(y, offset)
}
