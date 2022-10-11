package pdfcrate.components

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import java.io.File

class Image private constructor(
    private val file: File? = null,
    private val content: ByteArray? = null,
    private val name: String? = null,
    private val imageObject: PDImageXObject? = null,
    private val size: Size,
) : SizedComponent {

    companion object {
        @JvmStatic
        fun fromFile(file: File, size: Size) = Image(file = file, size = size)

        @JvmStatic
        fun fromPath(path: String, size: Size) = Image(file = File(path), size = size)

        @JvmStatic
        fun fromContent(content: ByteArray, name: String?, size: Size) =
            Image(content = content, name = name, size = size)

        @JvmStatic
        fun fromImageObject(imageObject: PDImageXObject, size: Size) =
            Image(imageObject = imageObject, size = size)
    }

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        return SizeBlocks.single(size.width, size.height)
    }

    override fun render(context: ComponentContext): Size {
        val wrapper = context.pages.contentStreamFor(context.y, size.height)

        val image = imageObject ?: if (file == null) {
            PDImageXObject.createFromByteArray(context.pages.document, content, name)
        } else {
            PDImageXObject.createFromFileByContent(file, context.pages.document)
        }
        wrapper.stream.drawImage(image, context.x, wrapper.realOffset - size.height, size.width, size.height)
        return size.copy()
    }
}
