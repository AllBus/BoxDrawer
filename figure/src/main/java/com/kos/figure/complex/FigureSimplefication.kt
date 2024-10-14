package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureComposition

class FigureSimplefication(override val figure: IFigure): FigureComposition() {
    override fun create(figure: IFigure): FigureComposition {
        return FigureSimplefication(figure)
    }

    override fun draw(g: IFigureGraphics) {
        g.setSimple(true)
        figure.draw(g)
        g.setSimple(false)
    }

    override fun print(): String {
        return "Упрощённое отображение"
    }

    override val hasTransform: Boolean
        get() = false
}