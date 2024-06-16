package com.kos.figure.collections

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix

class FigureList(
    private val figures: List<IFigure>
) : IFigure {

    override val count: Int
        get() = figures.size

    operator fun plus(figure: IFigure): FigureList {
        return FigureList(this.figures + figure)
    }

    operator fun plus(list: List<IFigure>): FigureList {
        return FigureList(this.figures + list)
    }

    operator fun plus(list: FigureList): FigureList {
        return FigureList(this.figures + list)
    }

    override fun list(): List<Figure> {
        return figures.flatMap { it.list() }
    }

    override fun rect(): BoundingRectangle {
        val l = figures.map { it.rect() }

        if (l.isEmpty()) {
            return BoundingRectangle.Empty
        }

        return l.fold(l.first()) { a, b -> a.union(b) }
    }

    override fun draw(g: IFigureGraphics) {
        figures.forEach { it.draw(g) }
    }

    fun simple(): FigureList {
        return FigureList(list())
    }

    override fun print(): String {
        return figures.joinToString(" ") { it.print() }
    }

    override fun collection(): List<IFigure> {
        return figures
    }

    override fun name(): String {
        return "Список"
    }

    override val transform: Matrix
        get() = Matrix.identity


    override val hasTransform: Boolean
        get() = false
}