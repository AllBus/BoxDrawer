package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure

class FigureColor(val color: Int,
                  val dxfColor:Int,
                  override val figure: IFigure) : FigureComposition() {

    override fun create(figure: IFigure): FigureComposition {
        return FigureColor(this.color, this.dxfColor, figure)
    }

    override fun draw(g: IFigureGraphics) {
        val c = g.getColor()
        g.setColor(color)
        figure.draw(g)
        g.setColor(c)
    }

    override fun print(): String {
        return "C $dxfColor (${figure.print()})"
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun name(): String {
        return "Цвет[$dxfColor](${color.toHexString()})"
    }

    override val hasTransform: Boolean
        get() = false

    override fun removeInner(inner: IFigure): IFigure {
        if (inner === figure)
            return FigureEmpty
        return FigureColor(color, dxfColor, figure.removeInner(inner))
    }

    override fun replaceInner(newCollection: List<IFigure>): IFigure {
        return if (newCollection.isEmpty())
            FigureEmpty
        else
            FigureColor(color, dxfColor, newCollection[0])
    }
}