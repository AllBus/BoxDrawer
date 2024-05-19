package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.IFigure
import vectors.Vec2
import vectors.BoundingRectangle as BoundingRectangle1

class FigureTranslate(
    override val figure: IFigure,
    val offset: Vec2,
): FigureComposition() {

    override fun create(figure: IFigure): FigureComposition {
        return FigureTranslate(figure, offset)
    }

    override fun rect(): BoundingRectangle1 {
        return figure.rect().translate(offset)
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureTranslate(
            figure,
            offset+ Vec2(translateX, translateY)
        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureTranslate(
            figure.rotate(angle),
            offset
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureTranslate(
            figure.rotate(angle, rotateCenter),
            offset
        )
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
}