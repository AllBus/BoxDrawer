package com.kos.boxdrawer.presentation

import java.lang.Math.pow
import kotlin.math.abs

object ZoomUtils {
    val zoomValues = listOf<Double>(
        1.0,
        1.2,
        1.5,
        1.75,
        2.0,
        2.25,
        2.5,
        2.8,
        3.25,
        4.0,
        4.5,
        5.0,
        6.0,
        7.25,
        8.5,
    )

    fun indexToZoom(index: Int): Double {
        val si = zoomValues.size
        val np = abs(index % si)
        val st = index / si


        return (pow(10.0, (st - 2).toDouble()) * zoomValues[np])
    }

    fun calcZoom(value: Double): Int {

        val p = when {
            value < 0.1 -> -2
            value < 1.0 -> -1
            else -> kotlin.math.log10(value).toInt()
        }

        val d = value / pow(10.0, p.toDouble())

        var n = 0
        var c = abs(zoomValues[0] - d)
        for (i in zoomValues.indices) {
            val lv = abs(zoomValues[i] - d)
            if (lv < c) {
                n = i
                c = lv
            }
        }

        return ((p + 2) * zoomValues.size + n)

        //return kotlin.math.log(value.toDouble(), 1.2).toFloat()
//    var i = 1
//    var m = 1.2
//    while (value>m){
//        m*=1.2
//        i++
//    }
//    return i.toFloat()
    }

    fun scale(currentScale: Double, delta: Int): Double {
        val z = calcZoom(currentScale) + delta
        if (z >= 0)
            return indexToZoom(z)
        else
            return currentScale
    }
}