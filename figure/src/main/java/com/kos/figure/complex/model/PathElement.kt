package com.kos.figure.complex.model

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import vectors.Vec2

interface PathElement {
    fun toFigure(): Figure
    fun toPath(): IFigurePath
    fun perimeter(): Double
    /**
     * @param delta в диапазоне (0.0 to 1.0)
     * @return равномерное перемещение по пути
    */
    fun positionInPath(delta: Double): PointWithNormal
    fun take(startMM:Double, endMM:Double):Figure
    fun translate(xy: Vec2): PathElement
    fun draw(g: IFigureGraphics)
    /**
     * Calculates the point on the path at the given parameter t (0.0 to 1.0).
     * @param t The parameter value (0.0 to 1.0). Н
     * @return точка на пути. Расстояние для сложных путей может изменяться не равномерно
     */
    fun pointAt(t: Double): Vec2

    val start:Vec2
    val end:Vec2
}