package com.kos.figure.composition.booleans

import com.kos.figure.Approximation
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.algorithms.UnionFigure

class FigureDiff(
    figure1: IFigure,
    figure2: IFigure,
    approximationSize: Int,
) : FigureUnion(figure1, figure2, approximationSize) {

    override fun recalculate(): IFigure {
        val newFigure: IFigure = when {
            figure1 == figure2 -> figure1
            figure1 == FigureEmpty -> FigureEmpty
            figure2 == FigureEmpty -> FigureEmpty
            else -> {
                return UnionFigure.diff(
                    approximations(figure1) ,
                    approximations(figure2) ,
                    approximationSize
                )
            }
        }
        return newFigure
    }
}