package pdfcrate.document

class Style(
    var textStyle: TextStyle,
    var lineStyle: LineStyle,
) {
    companion object {
        @JvmStatic
        fun default() = Style(
            TextStyle.default(),
            LineStyle.default(),
        )

    }
}

