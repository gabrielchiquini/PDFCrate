package pdfcrate.testutil

import io.mockk.every
import io.mockk.mockk
import pdfcrate.components.SizedComponent
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks

fun mockComponent(width: Float = 10f, height: Float = 10f): SizedComponent {
    val component = mockk<SizedComponent>()
    every { component.getBlocks(any()) } returns SizeBlocks.single(width, height)
    every { component.render(any()) } returns Size(width, height)
    return component
}
