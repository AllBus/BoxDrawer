package com.kos.figure.collections

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Approximation
import com.kos.figure.Figure
import com.kos.figure.FigureWithApproximation
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2


class FigureRing(
    val figures: List<FigureWithApproximation>
) : IFigure, Approximation {
    override val count: Int
        get() = figures.size

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.union(figures.map { it.rect() })
    }

    override fun draw(g: IFigureGraphics) {
        figures.forEach { it.draw(g) }
    }

    override fun print(): String {
        return figures.joinToString(" ") { it.print() }
    }

    override fun collection(): List<IFigure> {
        return figures
    }

    override fun name(): String {
        return "Ring"
    }

    override val transform: Matrix
        get() = Matrix.identity

    override val hasTransform: Boolean
        get() = false

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        if (figures.isEmpty()) {
            return emptyList()
        }
        val fg = figures.map { it.approximate(pointCount) }

        val fl = fg.fold(listOf<Vec2>()) { acc, next ->
            val nf = next.flatten()
            if (acc.isNotEmpty() && nf.isNotEmpty()) {
                val a = acc.last()
                if (nf.first() == a)
                    acc + nf.drop(1)
                else
                    acc + nf
            } else {
                nf
            }
        }
        return listOf(fl)
    }
}