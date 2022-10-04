package pdfcrate.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EdgesTest {
    @Test
    fun differentValues() {
        val edges = Edges(10f, 20f, 30f, 40f)
        assertThat(edges.top).isEqualTo(10f)
        assertThat(edges.right).isEqualTo(20f)
        assertThat(edges.bottom).isEqualTo(30f)
        assertThat(edges.left).isEqualTo(40f)
    }

    @Test
    fun symmetric() {
        val edges = Edges.symmetric(10f, 20f)
        assertThat(edges.top).isEqualTo(20f)
        assertThat(edges.right).isEqualTo(10f)
        assertThat(edges.bottom).isEqualTo(20f)
        assertThat(edges.left).isEqualTo(10f)
    }

    @Test
    fun all() {
        val edges = Edges.all(10f)
        assertThat(edges.top).isEqualTo(10f)
        assertThat(edges.right).isEqualTo(10f)
        assertThat(edges.bottom).isEqualTo(10f)
        assertThat(edges.left).isEqualTo(10f)
    }
}