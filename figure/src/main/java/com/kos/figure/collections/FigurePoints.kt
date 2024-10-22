package com.kos.figure.collections

import com.kos.drawer.IFigureGraphics
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigurePoints(val points: List<Vec2>, val radius: Double) : IFigure {
    override val count: Int
        get() = 1

    override fun rect(): BoundingRectangle {
        if (points.isEmpty()) return BoundingRectangle.Empty
        return BoundingRectangle.apply(points)
    }

    override fun draw(g: IFigureGraphics) {
        points.forEach { p ->
            g.drawCircle(p, radius)
        }
    }

    override fun print(): String {
        return "/points $radius ${points.joinToString(" "){ p -> "${p.x} ${p.y}" }}"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "Points ${points.size}"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false
}