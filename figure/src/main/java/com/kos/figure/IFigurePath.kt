package com.kos.figure

import vectors.BoundingRectangle
import vectors.Matrix
import vectors.PointWithNormal
import vectors.Vec2

interface IFigurePath {
    fun positionInPath(delta: Double): PointWithNormal
    fun positionInPath(edge: Int, delta: Double): PointWithNormal
    fun positionInPathAtMM(edge: Int, mm: Double): PointWithNormal{
        return positionInPath(edge, mm /pathLength())
    }
    fun pathLength():Double
    fun pathLength(edge: Int):Double
    fun edgeCount():Int
    fun path(edge:Int):IFigurePath
    fun startPoint(): Vec2
    fun endPoint(): Vec2
    /** Взять часть фигуры оступив */
    fun take(startMM:Double, endMM:Double):Figure
    fun duplicationAtNormal(h: Double): Figure
    fun rect(): BoundingRectangle
    fun toFigure(): Figure
    fun transform(matrix: Matrix): IFigurePath

}

interface IRotable{
    fun rotate(angle: Double): Figure

    fun rotate(angle: Double, rotateCenter: Vec2): Figure
}