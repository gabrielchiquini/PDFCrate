package pdfcrate.components

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import java.io.File

class ScalingImage private constructor(
    private val file: File? = null,
    private val content: ByteArray? = null,
    private val name: String? = null,
    private val width: Float = 0f,
    private val height: Float = 0f,
) : SizedComponent {
    private var imageObject: PDImageXObject? = null

    companion object {
        @JvmStatic
        fun fromWidth(path: String, width: Float) =
            ScalingImage(file = File(path), width = width)

        @JvmStatic
        fun fromWidth(file: File, width: Float) =
            ScalingImage(file = file, width = width)

        @JvmStatic
        fun fromWidth(content: ByteArray, name: String, width: Float) =
            ScalingImage(content = content, name = name, width = width)

        @JvmStatic
        fun fromHeight(path: String, height: Float) =
            ScalingImage(file = File(path), height = height)

        @JvmStatic
        fun fromHeight(file: File, height: Float) =
            ScalingImage(file = file, height = height)

        @JvmStatic
        fun fromHeight(content: ByteArray, name: String, height: Float) =
            ScalingImage(content = content, name = name, height = height)
    }

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val image = getImageObject(context)
        val size = size(image)
        return SizeBlocks.single(size.width, size.height)
    }

    override fun render(context: ComponentContext): Size {
        val image = getImageObject(context)
        val size = size(image)
        val delegate = Image.fromImageObject(image, size)
        return delegate.render(context)
    }

    private fun size(image: PDImageXObject) = if (width > 0f) {
        Size(width, width / image.width * image.height)
    } else {
        Size(height / image.height * image.width, height)
    }

    private fun getImageObject(context: ComponentContext): PDImageXObject {
        if (imageObject == null) {
            imageObject = if (file == null) {
                PDImageXObject.createFromByteArray(context.pages.document, content, name)
            } else {
                PDImageXObject.createFromFileByContent(file, context.pages.document)
            }
        }
        return imageObject!!
    }
}
