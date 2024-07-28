package com.kos.figure.complex

import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import vectors.Vec2

data class Edge(
    val a: Vec2,
    val b: Vec2,
) : IEdge {
    override fun toFigure(): FigureLine {
        return FigureLine(a, b)
    }

    override fun toPath(): IFigurePath = toFigure()

    override fun perimeter(): Double {
        return Vec2.distance(a, b)
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        return PointWithNormal.from(Vec2.lerp(a, b, delta), a, b)
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val d = Vec2.distance(a, b)

        if (d <= 0.0 || endMM<=startMM)
            return FigureEmpty

        val sm = startMM.coerceIn(0.0, d)
        val em = endMM.coerceIn(0.0, d)

        return FigureLine(
            Vec2.lerp(a, b, sm / d),
            Vec2.lerp(a, b, em / d)
        )
    }
}