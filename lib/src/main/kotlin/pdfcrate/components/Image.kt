package pdfcrate.components

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import pdfcrate.render.ComponentContext
import pdfcrate.util.Size
import pdfcrate.util.SizeBlocks
import java.io.File

/**
 * Embed an image into the document
 *
 * It can use its original size or scale the image keeping the aspect ratio or not
 */
class Image private constructor(
    private val file: File? = null,
    private val content: ByteArray? = null,
    private val name: String? = null,
    private val width: Float = 0f,
    private val height: Float = 0f,
) : SizedComponent {
    private var imageObject: PDImageXObject? = null

    companion object {
        /**
         * Uses the width to render the image keeping the aspect ratio
         */
        @JvmStatic
        fun fromWidth(path: String, width: Float) =
            Image(file = File(path), width = width)

        /**
         * Uses the width to render the image keeping the aspect ratio
         */
        @JvmStatic
        fun fromWidth(file: File, width: Float) =
            Image(file = file, width = width)

        /**
         * Uses the width to render the image keeping the aspect ratio
         */
        @JvmStatic
        fun fromWidth(content: ByteArray, name: String, width: Float) =
            Image(content = content, name = name, width = width)

        /**
         * Uses the height to render the image keeping the aspect ratio
         */
        @JvmStatic
        fun fromHeight(path: String, height: Float) =
            Image(file = File(path), height = height)

        /**
         * Uses the height to render the image keeping the aspect ratio
         */
        @JvmStatic
        fun fromHeight(file: File, height: Float) =
            Image(file = file, height = height)

        /**
         * Uses the height to render the image keeping the aspect ratio
         */
        @JvmStatic
        fun fromHeight(content: ByteArray, name: String, height: Float) =
            Image(content = content, name = name, height = height)

        /**
         * Renders the image using the specified width and height, stretching the image
         */
        @JvmStatic
        fun scale(file: File, width: Float, height: Float) =
            Image(file = file, width = width, height = height)

        /**
         * Renders the image using the specified width and height, stretching the image
         */
        @JvmStatic
        fun scale(path: String, width: Float, height: Float) =
            Image(file = File(path), width = width, height = height)

        /**
         * Renders the image using the specified width and height, stretching the image
         */
        @JvmStatic
        fun scale(content: ByteArray, name: String, width: Float, height: Float) =
            Image(content = content, name = name, width = width, height = height)


        /**
         * Renders the image using its original size
         */
        @JvmStatic
        fun originalSize(file: File) =
            Image(file = file)

        /**
         * Renders the image using its original size
         */
        @JvmStatic
        fun originalSize(path: String) =
            Image(file = File(path))

        /**
         * Renders the image using its original size
         */
        @JvmStatic
        fun originalSize(content: ByteArray, name: String) =
            Image(content = content, name = name)

    }

    override fun getBlocks(context: ComponentContext): SizeBlocks {
        val image = getImageObject(context)
        val size = size(image)
        return SizeBlocks.single(size.width, size.height)
    }

    override fun render(context: ComponentContext): Size {
        val image = getImageObject(context)
        val size = size(image)
        val wrapper = context.pages.contentStreamFor(context.y, size.height)
        wrapper.stream.drawImage(image, context.x, wrapper.realOffset - size.height, size.width, size.height)
        return Size(size.width, wrapper.virtualOffset + size.height - context.y)
    }

    private fun size(image: PDImageXObject) =
        if (width > 0f && height > 0f) {
            Size(width, height)
        } else if (width > 0f) {
            Size(width, width / image.width * image.height)
        } else if (height > 0f) {
            Size(height / image.height * image.width, height)
        } else {
            Size(image.height.toFloat(), image.width.toFloat())
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
