package pdfcrate.document

class Style(
    val textStyle: TextStyle,
    val lineStyle: LineStyle,
) {
    companion object {
        @JvmStatic
        val DEFAULT_STYLE: Style = Style(
            TextStyle.DEFAULT_STYLE,
            LineStyle.DEFAULT_STYLE,
        )

    }
}

