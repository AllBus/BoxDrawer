package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Approximation
import com.kos.figure.IFigure
import vectors.Matrix
import vectors.Vec2
import vectors.BoundingRectangle as BoundingRectangle1

class FigureTranslate(
    override val figure: IFigure,
    val offset: Vec2,
) : FigureComposition(), Approximation {

    override fun create(figure: IFigure): FigureComposition {
        return FigureTranslate(figure, offset)
    }

    override fun rect(): BoundingRectangle1 {
        return figure.rect().translate(offset)
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.translate(offset.x, offset.y)
        figure.draw(g)
        g.restore()
    }

    override fun print(): String {
        return "T (${figure.print()})"
    }

    override fun name(): String {
        return "Сдвиг $offset"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return if (figure is Approximation)
            figure.approximate(pointCount).map { v -> v.map { it + offset } }
        else
            emptyList()
    }

    override val transform: Matrix
        get() = Matrix.translate(offset.x, offset.y)

    override val hasTransform: Boolean
        get() = true
}

