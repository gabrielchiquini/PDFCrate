package pdfcrate.exceptions

import java.lang.RuntimeException

class NotEnoughSizeException(override val message: String?) : RuntimeException()