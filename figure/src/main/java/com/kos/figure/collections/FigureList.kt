package com.kos.figure.collections

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.FigureEmpty
import com.kos.figure.ICropable
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix

class FigureList(
    private val figures: List<IFigure>
) : IFigure, ICropable {

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
        val l = mutableListOf<IFigure>()
        simpleList(this, l)
        return FigureList(l.toList())
    }

    private fun simpleList(figure: FigureList, resultList: MutableList<IFigure>) {
        figure.figures.forEach { f ->
            when (f) {
                is FigureList -> simpleList(f, resultList)
                is FigureEmpty -> {}
                else -> resultList.add(f)
            }
        }
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

    override fun crop(k: Double, cropSide: CropSide): FigureList {
        return FigureList(figures.filterIsInstance<ICropable>().map { it.crop(k, cropSide) })
    }

    override fun removeInner(inner: IFigure): IFigure {
        val fm = figures.filter { it !== inner }
        return if (fm.size != figures.size)
            replaceInner(fm)
        else
            replaceInner(figures.map { it.removeInner(inner) })

    }

    override fun replaceInner(newCollection: List<IFigure>): IFigure {
        return FigureList(newCollection)
    }
}

fun <T : IFigure> Iterable<T>.toFigure(): FigureList = FigureList(this.toList())