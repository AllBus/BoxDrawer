package com.kos.boxdrawe.presentation

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.kos.figure.IFigure
import com.kos.figure.complex.FigureImage
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

object ImageUtils {

    data class ImageMetadata(
        val width: Int,
        val height: Int,
        val format: Int
    )

    fun supportedFormats():Array<String>{
        val readerFormats = ImageIO.getReaderFormatNames()
        return readerFormats
    }

    fun getImageMetadata(filePath: String): ImageMetadata? {
        val imageFile = File(filePath)
        return try {val bufferedImage: BufferedImage = ImageIO.read(imageFile)
            ImageMetadata(
                width = bufferedImage.width,
                height = bufferedImage.height,
                format = bufferedImage.colorModel.pixelSize
            )
        } catch (e: Exception) {
            null
        }
    }

    fun formatOfData(name:String, bufferedImage: BufferedImage ):String{
        return when {
            name.endsWith(".png") -> "png"
            name.endsWith(".jpg") || name.endsWith(".jpeg") -> "jpg"
            name.endsWith(".gif") -> "gif"
            name.endsWith(".tiff") -> "tiff"
            name.endsWith(".bmp") -> "bmp"
            name.endsWith(".webp") -> "webp"
            bufferedImage.colorModel.pixelSize == 24 -> "bmp"
            bufferedImage.colorModel.pixelSize == 32 -> "png"
            else -> "png"
        }

    }

    fun convertToBmp(image: BufferedImage, srcFormat:String): BufferedImage? {
        val resultImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val rgb = image.getRGB(x, y)
                resultImage.setRGB(x, y, rgb)
            }
        }

        return resultImage
    }

    fun bufferedImageToImageBitmap(bufferedImage: BufferedImage, srcFormat:String): ImageBitmap? {
        try {
            val argbArray = IntArray(bufferedImage.width * bufferedImage.height)
            bufferedImage.getRGB(
                0,
                0,
                bufferedImage.width,
                bufferedImage.height,
                argbArray,
                0,
                bufferedImage.width
            )

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, srcFormat, outputStream)
            val byteArray = outputStream.toByteArray()
            val bitmap = loadImageBitmap(ByteArrayInputStream(byteArray))

            return bitmap
        } catch (e:Exception){
            e.printStackTrace()
            return null
        }
    }

    fun loadImageFromFile(filePath:String): BufferedImage? {
        val imageFile = File(filePath)
        return try {
            ImageIO.read(imageFile)
        } catch (e: Exception) {
            println("Error loading image: ${e.message}")
            null
        }
    }

    fun collectImages(src: IFigure):List<FigureImage>{
        if (src is FigureImage)
            return listOf(src)
        return src.collection().flatMap {
            collectImages(it)
        }
    }
}