package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.Vec2

class FigureSpline(points: List<Vec2>) : FigurePolygon(points) {

    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureSpline(points)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawSpline(points)
    }

    override fun print(): String {
        return "M 0 0 s " + points.map { p -> "${p.x} ${p.y}" }.joinToString(" ")
    }

    private val length: List<Double> by lazy { calculateLength() }

    override fun positionInPath(delta: Double): PointWithNormal {
        val l = length
        return if (l.isNotEmpty()) {
            // Todo вычислить координаты. Сейчас они относительно напрявляющей полилинии
            val fl = l.last()
            val fp = fl * delta
            if (fp >= fl)
                PointWithNormal(points.lastOrNull() ?: Vec2.Zero, Vec2.Zero)
            else {
                val i = l.indexOfLast { it <= fp }

                val pd = if (i >= 0) l[i] else 0.0

                val pde = (l.getOrNull(i + 1) ?: fl)
                val c = (fp - pd) / (pde - pd)

                PointWithNormal(Vec2.lerp(points[i + 1], points[i + 2], c), Vec2.Zero)
            }
        } else
            PointWithNormal(points.firstOrNull() ?: Vec2.Zero, Vec2.Zero)
    }

    fun calculateLength(): List<Double> {
        return points.windowed(4, 3) { p ->
            Vec2.bezierLength(p)
        }
    }

    override fun name(): String {
        return "Сплайн"
    }
}