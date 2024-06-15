package com.kos.figure.composition.booleans

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Approximation
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.algorithms.UnionFigure
import com.kos.figure.composition.FigureComposition
import com.kos.figure.composition.FigureTranslate
import vectors.Vec2

open class FigureUnion(
    val figure1: IFigure,
    val figure2: IFigure,
    val approximationSize: Int,
) : FigureComposition(), Approximation {

    private var unionFigure: IFigure? = null

    fun recalculateFigure(): IFigure {
        val newFigure: IFigure = recalculate()
        unionFigure = newFigure
        return newFigure
    }

    protected fun approximations(figure: IFigure): List<Approximation> {
        return figure.list().filterIsInstance(Approximation::class.java)
    }

    protected open fun recalculate(): IFigure {
        val newFigure: IFigure = when {
            figure1 == figure2 -> figure1
            figure1 == FigureEmpty -> figure2
            figure2 == FigureEmpty -> figure1
            else -> {
                return UnionFigure.union(
                    approximations(figure1) + approximations(figure2),
                    approximationSize
                )
            }
        }
        return newFigure
    }

    override val figure: IFigure
        get() = unionFigure ?: recalculateFigure()

    override fun create(figure: IFigure): FigureComposition {
        return FigureTranslate(figure, Vec2.Zero)
    }

    override fun draw(g: IFigureGraphics) {
        figure.draw(g)
    }

    override fun print(): String {
        return "Объединние"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return (figure as? Approximation)?.approximate(pointCount) ?: emptyList()
    }
}