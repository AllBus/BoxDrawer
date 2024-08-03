package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import vectors.Vec2

interface IEdge {
    fun toFigure(): Figure
    fun toPath(): IFigurePath
    fun perimeter(): Double
    fun positionInPath(delta: Double): PointWithNormal
    fun take(startMM:Double, endMM:Double):Figure
    fun translate(xy: Vec2):IEdge
    fun draw(g: IFigureGraphics)
    val start:Vec2
    val end:Vec2
}