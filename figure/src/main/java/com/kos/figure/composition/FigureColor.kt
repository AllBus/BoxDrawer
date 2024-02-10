package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Vec2

class FigureColor(val color: Int, val figures: IFigure) : IFigure {
    override val count: Int
        get() = figures.count

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return  this
    }

    override fun list(): List<Figure> {
        return figures.list()
    }

    override fun rect(): BoundingRectangle {
        return figures.rect()
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureColor(color, figures.translate(translateX, translateY))
    }

    override fun rotate(angle: Double): IFigure {
        return FigureColor(color, figures.rotate(angle))
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureColor(color, figures.rotate(angle, rotateCenter))
    }

    override fun draw(g: IFigureGraphics) {
        val c = g.getColor()
        g.setColor(color)
        figures.draw(g)
        g.setColor(c)
    }

}