package com.kos.figure.segments.model

import com.kos.drawer.IFigureGraphics
import vectors.PointWithNormal
import vectors.Vec2

object EmptyPath : PathElement {
    override fun perimeter(): Double = 0.0

    override fun positionInPath(delta: Double): PointWithNormal = PointWithNormal.EMPTY

    override fun translate(xy: Vec2): PathElement = EmptyPath

    override fun draw(g: IFigureGraphics) {}

    override fun pointAt(t: Double): Vec2 = Vec2.Zero

    override val start: Vec2 = Vec2.Zero

    override val end: Vec2 = Vec2.Zero

    /** До пустого пути дистанция должна уходить в бесконечность*/
    override fun distance(point: Vec2): Double = 10e200

    override val center: Vec2
        get() = Vec2.Zero
}