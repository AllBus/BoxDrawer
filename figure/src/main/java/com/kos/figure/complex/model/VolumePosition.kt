package com.kos.figure.complex.model

import com.kos.figure.IFigure
import vectors.Matrix

class VolumePosition(
    val figure:IFigure,
    val x: Double,
    val y: Double,
    val z: Double,
    val rotateX: Float,
    val rotateY: Float,
    val rotateZ: Float,
) {

    val transform: Matrix get() {
        val mr = Matrix()
        mr.translate(x.toFloat(), y.toFloat(), z.toFloat())
        mr.rotateX(rotateX)
        mr.rotateY(rotateY)
        mr.rotateZ(rotateZ)
        return mr
    }

    fun toRotateMatrix(): Matrix {
        val mr = Matrix()
        mr.rotateX(rotateX)
        mr.rotateY(rotateY)
        mr.rotateZ(rotateZ)
        return mr
    }

    companion object {
        fun onXY(figure:IFigure, x: Double, y: Double, z:Double): VolumePosition {
            return VolumePosition(figure, x, y, z, 0.0f, 0.0f, 0.0f)
        }

        fun onXZ(figure:IFigure, x: Double, y: Double, z:Double): VolumePosition {
            return VolumePosition(figure, x, y, z, 90.0f, 0.0f, 0.0f)
        }

        fun onYZ(figure:IFigure, x: Double, y: Double, z:Double): VolumePosition {
            return VolumePosition(figure, x, y, z, 0.0f, 0.0f, -90.0f)
        }
    }
}