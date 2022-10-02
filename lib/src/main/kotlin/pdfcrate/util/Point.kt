package pdfcrate.util

data class Point(val x: Float, val y: Float) {
    operator fun minus(point: Point): Point {
        return Point(x - point.x, y - point.y)
    }
}