package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.Vec2

class FigureSpline(points: List<Vec2>): FigurePolygon(points) {

    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureSpline(points)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawSpline(points)
    }

    override fun print(): String {
        return "M 0 0 s "+points.map { p ->"${p.x} ${p.y}" }.joinToString(" ")
    }
}