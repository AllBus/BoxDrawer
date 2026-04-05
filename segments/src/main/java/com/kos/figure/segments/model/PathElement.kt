package com.kos.figure.segments.model

import com.kos.drawer.IFigureGraphics
import vectors.PointWithNormal
import vectors.Vec2

interface PathElement {
    fun perimeter(): Double

    /**
     * @param delta в диапазоне (0.0 to 1.0)
     * @return равномерное перемещение по пути
     */
    fun positionInPath(delta: Double): PointWithNormal
    fun translate(xy: Vec2): PathElement
    fun draw(g: IFigureGraphics)

    /**
     * Расстояние от точки до ближайшей точки кривой
     */
    fun distance(point: Vec2): Double

    /**
     * Calculates the point on the path at the given parameter t (0.0 to 1.0).
     * @param t The parameter value (0.0 to 1.0). Н
     * @return точка на пути. Расстояние для сложных путей может изменяться не равномерно
     */
    fun pointAt(t: Double): Vec2

    val start: Vec2
    val end: Vec2

    val center: Vec2
}