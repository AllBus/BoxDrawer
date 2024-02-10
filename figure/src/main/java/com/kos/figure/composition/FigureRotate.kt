package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Vec2

class FigureRotate(
    val figure: IFigure,
    val angle: Double,
    val pivot: Vec2,
): IFigure {
    override val count: Int
        get() = figure.count

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return this // TODO("Not yet implemented")
    }

    override fun list(): List<Figure> {
        return figure.list()
    }

    override fun rect(): BoundingRectangle {
        return figure.rect().translate(pivot)
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureTranslate(this,
            Vec2(translateX, translateY)
        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureRotate(
            figure.rotate(angle),
            this.angle,
            pivot
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureRotate(
            figure.rotate(angle, rotateCenter),
            this.angle,
            pivot
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.rotate(angle,  pivot)
        figure.draw(g)
        g.restore()
    }
}