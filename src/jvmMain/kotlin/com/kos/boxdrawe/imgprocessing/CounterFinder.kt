package com.kos.boxdrawe.imgprocessing

import scala.collection.mutable.Stack
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min

object CounterFinder {

    fun finPoints(image: BufferedImage, maxSize:Int, maxSize2:Int):List<List<PointInfo>>{

           // Mat
      //  val contours = ArrayList<MatOfPoint>()
        //val hierarchy = Mat()
       // Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        val labels = labelRegions(image,Color.BLACK.rgb )

        val regionBoundaries = mutableMapOf<Int, Rectangle>()
        val height = image.height
        val width = image.width


        for (y in 0 until height) {
            for (x in 0 until width) {
                val label = labels[y][x]
                if (label != 0) {
                    val rect = regionBoundaries.getOrPut(label) { Rectangle(x, y, x, y) }
                    rect.x = min(rect.x, x)
                    rect.y = min(rect.y, y)
                    rect.right = max(rect.right, x )
                    rect.bottom = max(rect.bottom, y)
                }
            }
        }


        val filtered = regionBoundaries.filter {
            it.value.height<maxSize && it.value.width<maxSize
        }

        val filtered2 = regionBoundaries.filter {
            it.value.height>maxSize && it.value.width>maxSize &&
            it.value.height<maxSize2 && it.value.width<maxSize2
        }


        val a = filtered.map {
           PointInfo(it.value.x+it.value.width/2,it.value.y+it.value.height/2,it.value.width)
        }
        val b = filtered2.map {
            PointInfo(it.value.x+it.value.width/2,it.value.y+it.value.height/2,it.value.width)
        }


        return listOf(a, b)
    }

    fun labelRegions(image: BufferedImage,findedColor:Int): Array<IntArray> {
        val width = image.width
        val height = image.height
        val labels = Array(height) { IntArray(width) } // Stores labels for each pixel
        var nextLabel = 1 // Starting label

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (isSimilarToBlack(image.getRGB(x, y), 60) && labels[y][x] == 0) { // Black pixel and not labeled yet
                    labelRegion(image, labels, x, y, nextLabel++, findedColor) // Label the connected region
                }
            }
        }

        return labels
    }

    fun labelRegion(image: BufferedImage, labels: Array<IntArray>, sx: Int, sy: Int, label: Int, findedColor:Int) {

        val labelStack = java.util.Stack<Pair<Int,Int>>()
        labelStack.push(sx to sy)

        while (labelStack.isNotEmpty()) {
            val (x, y) = labelStack.pop()

            if (x < 0 || x >= image.width || y < 0 || y >= image.height ||
                !isSimilarToBlack(image.getRGB(x, y), 60) || labels[y][x] != 0
            ) {
                continue // Out of bounds, not black, or already labeled
            }

            labels[y][x] = label // Assign label to current pixel

            // Recursively label neighboring pixels
            labelStack.push( x + 1 to y)
            labelStack.push( x - 1 to y)
            labelStack.push( x to y + 1)
            labelStack.push( x to y - 1)
        }
    }

    fun isSimilarToBlack(color: Int, tolerance: Int): Boolean {
        val red = Color(color).red
        val green = Color(color).green
        val blue = Color(color).blue

        return red <= tolerance && green <= tolerance && blue <= tolerance
    }

}

class Rectangle(var x: Int, var y: Int, var right: Int, var bottom: Int){
    val width: Int get() = right - x
    val height: Int get() = bottom - y
}

class PointInfo(val x:Int, val y:Int, val size:Int)