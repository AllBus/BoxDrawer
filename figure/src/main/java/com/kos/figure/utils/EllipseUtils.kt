package com.kos.figure.utils

import kotlin.math.PI

object EllipseUtils {
    /**
     * 0 .. 2*PI to  0 .. 4*PI
     */
    fun normalize(
        start: Double,
        sweep: Double,
    ): Pair<Double, Double> {
        val sww = sweep + start
        return if (sweep < 0) { /* -2*PI .. 2*PI to  -2*PI .. 4*PI */
            if (sww < 0.0)
                Pair(sww + 2 * PI, start + 2 * PI)
            else
                Pair(sww, start)
        } else {
            Pair(start, sww)
        }
    }

    fun normalizeAngle(angle: Double): Double {
        val pi2 = PI * 2
        return (angle % pi2 + pi2) % pi2
    }
}