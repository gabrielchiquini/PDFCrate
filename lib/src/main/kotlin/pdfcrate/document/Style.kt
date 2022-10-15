package pdfcrate.document

class Style(
    var textStyle: TextStyle,
    var lineStyle: LineStyle,
) {
    companion object {
        @JvmStatic
        val DEFAULT_STYLE: Style = Style(
            TextStyle.DEFAULT_STYLE,
            LineStyle.DEFAULT_STYLE,
        )

    }
}

