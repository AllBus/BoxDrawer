package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Vec2

class FigureColor(val color: Int, override val figure: IFigure) : FigureComposition() {

    override fun create(figure: IFigure): FigureComposition {
        return FigureColor(this.color, figure)
    }

    override fun draw(g: IFigureGraphics) {
        val c = g.getColor()
        g.setColor(color)
        figure.draw(g)
        g.setColor(c)
    }

    override fun print(): String {
        return "C (${figure.print()})"
    }

}