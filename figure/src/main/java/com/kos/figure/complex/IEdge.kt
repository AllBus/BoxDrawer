package com.kos.figure.complex

import com.kos.figure.Figure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal

interface IEdge {
    fun toFigure(): Figure
    fun toPath(): IFigurePath
    fun perimeter(): Double
    fun positionInPath(delta: Double): PointWithNormal
    fun take(startMM:Double, endMM:Double):Figure
}