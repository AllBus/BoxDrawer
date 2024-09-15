package com.kos.boxdrawe.presentation
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.kos.boxdrawe.widget.NumericTextFieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

interface ImageAction{
    class ActionRotate(val degrees:Double): ImageAction
    class ActionGrayScale(): ImageAction
    class ActionBounds(): ImageAction
    class ActionContrast(val contrastFactor:Double): ImageAction
    class ActionGaussian(val sigma:Double): ImageAction
    class ActionNone():ImageAction
}

class ImageToolsData(val tools: ITools) {

    private val currentAction = MutableStateFlow<ImageAction>(ImageAction.ActionNone())

    val contrastState = NumericTextFieldState(
        50.0,
        maxValue = 300.0
    ){ v ->
        currentAction.value = ImageAction.ActionContrast(v*0.01)
    }

    val gaussianState = NumericTextFieldState(
        1.0,
        maxValue = 300.0
    ){ v ->
        currentAction.value = ImageAction.ActionGaussian(v*0.1)
    }

    val rotateState = NumericTextFieldState(
        0.0,
        minValue = -360.0,
        maxValue = 360.0
    ){ v ->
        currentAction.value = ImageAction.ActionRotate(v)
    }

    fun actionGrayScale(enable:Boolean){
        currentAction.value = if (enable)
            ImageAction.ActionGrayScale()
        else
            ImageAction.ActionNone()
    }

    fun actionBounds(enable:Boolean){
        currentAction.value = if (enable)
            ImageAction.ActionBounds()
        else
            ImageAction.ActionNone()
    }



    private var sourceImage : BufferedImage? = null
    private var modifierImage : BufferedImage? = null


    val sourceBitmap = MutableStateFlow<ImageBitmap?>(null)

    val currentImage = currentAction.mapLatest { act ->
        modifierImage?.let { mi ->
            try {
                when (act) {
                    is ImageAction.ActionRotate -> rotateBitmap(mi, act.degrees)
                    is ImageAction.ActionContrast -> changeContrast(mi, act.contrastFactor)
                    is ImageAction.ActionGrayScale -> convertToBlackAndWhite(mi)
                    is ImageAction.ActionGaussian -> gaussianBlur(mi, 5, act.sigma)
                    is ImageAction.ActionBounds -> grad(mi)
                    else -> mi
                }
            }catch (e:Exception){
                null
            }
        }

    }

    val currentBitmap = currentImage.mapLatest { it?.let{ v -> bufferedImageToImageBitmap(v) } }

    fun loadImage(file: String) {
        sourceImage= loadImageFromFile(file)
        sourceBitmap.value =sourceImage?.let{ bufferedImageToImageBitmap(it)}

        modifierImage = sourceImage
        currentAction.value = ImageAction.ActionNone()

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

    fun rotateBitmap(bitmap: BufferedImage, degrees: Double): BufferedImage {
        val radians = Math.toRadians(degrees)
        val sin = sin(radians)
        val cos = cos(radians)

        val width = bitmap.width
        val height = bitmap.height

        val newWidth = (abs(width * cos) + abs(height * sin)).toInt()
        val newHeight = (abs(width * sin) + abs(height * cos)).toInt()

        val rotatedBitmap = BufferedImage(newWidth, newHeight, bitmap.type)
        val centerX = width / 2.0
        val centerY = height / 2.0

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                val rotatedX =(cos * (x - newWidth / 2.0) - sin * (y - newHeight / 2.0) + centerX).toInt()
                val rotatedY = (sin * (x - newWidth / 2.0) + cos * (y - newHeight / 2.0) + centerY).toInt()

                if (rotatedX in 0 until width && rotatedY in 0 until height) {
                    rotatedBitmap.setRGB(x, y, bitmap.getRGB(rotatedX, rotatedY))
                }
            }
        }

        return rotatedBitmap
    }

    fun changeContrast(image: BufferedImage, contrastFactor: Double): BufferedImage {
        val resultImage= BufferedImage(image.width, image.height, image.type)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val pixel = image.getRGB(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                val newRed = (0.5 + contrastFactor * (red / 255.0 - 0.5)).coerceIn(0.0, 1.0) * 255.0
                val newGreen = (0.5 + contrastFactor * (green / 255.0 - 0.5)).coerceIn(0.0, 1.0) * 255.0
                val newBlue = (0.5 + contrastFactor * (blue / 255.0 - 0.5)).coerceIn(0.0, 1.0) * 255.0

                val newPixel = (newRed.toInt() shl 16) or (newGreen.toInt() shl 8) or newBlue.toInt()
                resultImage.setRGB(x, y, newPixel)
            }
        }

        return resultImage
    }

    fun convertToBlackAndWhite(image: BufferedImage): BufferedImage {
        val resultImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_BYTE_GRAY)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val rgb = image.getRGB(x, y)
                val red = (rgb shr 16) and 0xFF
                val green = (rgb shr 8) and 0xFF
                val blue = rgb and 0xFF

                val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
                val newRgb = (gray shl 16) or (gray shl 8) or gray
                resultImage.setRGB(x, y, newRgb)
            }
        }

        return resultImage
    }

    fun bufferedImageToImageBitmap(bufferedImage: BufferedImage): ImageBitmap? {
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
            ImageIO.write(bufferedImage, "bmp", outputStream)
            val byteArray = outputStream.toByteArray()
            val bitmap = loadImageBitmap(ByteArrayInputStream(byteArray))

            return bitmap
        }catch (e:Exception){
            return null
        }
    }

    suspend fun applyChanges() {
        modifierImage = currentImage.first()
    }

    fun gaussianBlur(grayscaleImage: BufferedImage, kernelSize: Int, sigma: Double): BufferedImage {
        val blurredImage = BufferedImage(grayscaleImage.width, grayscaleImage.height, BufferedImage.TYPE_BYTE_GRAY)
        val kernel = calculateGaussianKernelWeights(kernelSize, sigma)
        val center = kernelSize / 2

        for (x in center until grayscaleImage.width - center) {
            for (y in center until grayscaleImage.height - center) {
                var sum = 0.0
                for (i in -center..center) {
                    for (j in -center..center) {
                        val pixelValue = Color(grayscaleImage.getRGB(x+ i, y + j)).red
                        sum += pixelValue * kernel[i + center][j + center]
                    }
                }
                val blurredValue = sum.roundToInt()
                blurredImage.setRGB(x, y, Color(blurredValue, blurredValue, blurredValue).rgb)
            }
        }

        return blurredImage
    }

    fun calculateGaussianKernelWeights(kernelSize: Int, sigma: Double): Array<DoubleArray> {
        val kernel = Array(kernelSize) { DoubleArray(kernelSize) }
        val center = kernelSize / 2
        var sum = 0.0

        for (x in 0 until kernelSize) {for (y in 0 until kernelSize) {
            val distanceSquared = (x - center).toDouble().pow(2.0) + (y - center).toDouble().pow(2.0)
            kernel[x][y] = exp(-distanceSquared / (2 * sigma.pow(2.0))) / (2 * Math.PI * sigma.pow(2.0))
            sum += kernel[x][y]
        }
        }

        // Normalize the kernel weights
        for (x in 0 until kernelSize) {
            for (y in 0 until kernelSize) {
                kernel[x][y] /= sum
            }
        }

        return kernel
    }

    fun grad(blurredImage: BufferedImage): BufferedImage{
        val (gradientX, gradientY) = calculateGradients(blurredImage)
        val gradientMagnitude = calculateGradientMagnitude(gradientX, gradientY)
        val suppressedImage = nonMaximumSuppression(gradientMagnitude, gradientX, gradientY)

        /*
        Threshold Values: You define two threshold values: lowThreshold and highThreshold.
Pixel Classification: You iterate through the suppressed image and classify each pixel based on its gradient magnitude:
Strong Edge: Pixels with magnitudes greater than or equal to highThreshold are marked as strong edges (value 255).
Weak Edge: Pixels with magnitudes between lowThreshold and highThreshold are marked as weak edges (value 128).
Non-Edge: Pixels with magnitudes below lowThreshold are considered non-edges (value 0).

         */
        val lowThreshold = 50 // Example low threshold
        val highThreshold = 150 // Example high threshold
        val thresholdedImage = doubleThresholding(suppressedImage, lowThreshold, highThreshold)
        val finalImage = edgeTrackingByHysteresis(thresholdedImage)
        return finalImage
    }

    fun calculateGradients(blurredImage: BufferedImage): Pair<Array<IntArray>, Array<IntArray>> {
        val width = blurredImage.width
        val height = blurredImage.height
        val gradientX = Array(width) { IntArray(height) }
        val gradientY = Array(width) { IntArray(height) }

        val sobelX = arrayOf(
            intArrayOf(-1, 0, 1),
            intArrayOf(-2, 0, 2),
            intArrayOf(-1, 0, 1)
        )

        val sobelY = arrayOf(
            intArrayOf(-1, -2, -1),
            intArrayOf(0, 0, 0),
            intArrayOf(1, 2, 1)
        )

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                var gx = 0
                var gy = 0
                for (i in -1..1) {
                    for (j in -1..1) {
                        val pixelValue = Color(blurredImage.getRGB(x + i, y + j)).red
                        gx += pixelValue * sobelX[i + 1][j + 1]
                        gy += pixelValue * sobelY[i + 1][j + 1]
                    }
                }
                gradientX[x][y] = gx
                gradientY[x][y] = gy
            }
        }

        return Pair(gradientX, gradientY)
    }



    fun calculateGradientMagnitude(gradientX: Array<IntArray>, gradientY: Array<IntArray>): Array<IntArray> {
        val width = gradientX.size
        val height = gradientX[0].size
        val gradientMagnitude = Array(width) { IntArray(height) }

        for (x in 0 until width) {
            for (y in 0 until height) {
                gradientMagnitude[x][y] = hypot(gradientX[x][y].toDouble(), gradientY[x][y].toDouble()).roundToInt()
            }
        }

        return gradientMagnitude
    }

    fun nonMaximumSuppression(gradientMagnitude: Array<IntArray>, gradientX: Array<IntArray>, gradientY: Array<IntArray>): Array<IntArray> {
        val width = gradientMagnitude.size
        val height = gradientMagnitude[0].size
        val suppressedImage = Array(width) { IntArray(height) }

        for (x in 1 until width - 1) {
            for (y in 1 until height - 1) {
                val mag = gradientMagnitude[x][y]
                val gx = gradientX[x][y]
                val gy = gradientY[x][y]

                // Calculate gradient direction (angle)
                val angle = Math.atan2(gy.toDouble(), gx.toDouble()) * (180.0 / Math.PI)

                // Quantize angle into 4 directions
                val direction= when {
                    (angle >= -22.5 && angle < 22.5) || (angle >= 157.5 || angle < -157.5) -> 0 // East-West
                    (angle >= 22.5 && angle < 67.5) || (angle >= -157.5 && angle < -112.5) -> 1 // North-East, South-West
                    (angle >= 67.5 && angle < 112.5) || (angle >= -112.5 && angle < -67.5) -> 2 // North-South
                    else -> 3 // North-West, South-East
                }

                // Compare with neighbors along gradient direction
                val isMaximum = when (direction) {
                    0 -> mag >= gradientMagnitude[x + 1][y] && mag >= gradientMagnitude[x - 1][y]
                    1 -> mag >= gradientMagnitude[x + 1][y - 1] && mag >= gradientMagnitude[x - 1][y + 1]
                    2 -> mag >= gradientMagnitude[x][y + 1] && mag >= gradientMagnitude[x][y - 1]
                    else -> mag >= gradientMagnitude[x - 1][y - 1] && mag >= gradientMagnitude[x + 1][y + 1]
                }

                // Suppress non-maximum pixels
                suppressedImage[x][y] = if (isMaximum) mag else 0
            }
        }

        return suppressedImage
    }

    fun doubleThresholding(suppressedImage: Array<IntArray>, lowThreshold: Int, highThreshold: Int): Array<IntArray> {
        val width = suppressedImage.size
        val height = suppressedImage[0].size
        val thresholdedImage = Array(width) { IntArray(height) }

        for (x in 0 until width) {
            for (y in 0 until height) {
                val mag = suppressedImage[x][y]
                thresholdedImage[x][y] = when {
                    mag >= highThreshold -> 255 // Strong edge
                    mag in lowThreshold until highThreshold -> 128 // Weak edge
                    else -> 0 // Non-edge
                }
            }
        }

        return thresholdedImage
    }

    fun edgeTrackingByHysteresis(thresholdedImage: Array<IntArray>): BufferedImage {
        val width = thresholdedImage.size
        val height = thresholdedImage[0].size
        val finalImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        fun checkNeighbors(x: Int, y: Int) {
            if (x in 0 until width && y in 0 until height && thresholdedImage[x][y] == 128) {
                thresholdedImage[x][y] = 255 // Mark weak edge as strong
                // Recursively check neighbors
                for (i in -1..1) {
                    for (j in -1..1) {
                        checkNeighbors(x + i, y + j)
                    }
                }
            }
        }

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (thresholdedImage[x][y] == 255) { // Start tracking from strong edges
                    checkNeighbors(x, y)
                }
            }
        }

        // Set pixel values in the final image
        for (x in 0 until width) {
            for (y in 0 until height) {
                val value = thresholdedImage[x][y]
                finalImage.setRGB(x, y, Color(value, value, value).rgb)
            }
        }

        return finalImage
    }
}