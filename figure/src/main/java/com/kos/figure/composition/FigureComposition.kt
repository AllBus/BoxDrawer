package com.kos.figure.composition

import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Vec2

abstract class FigureComposition : IFigure {

    abstract val figure: IFigure

    abstract fun create(figure: IFigure): FigureComposition

    override val count: Int
        get() = figure.count

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return  this
    }

    override fun list(): List<Figure> {
        return figure.list()
    }

    override fun rect(): BoundingRectangle {
        return figure.rect()
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return create( figure.translate(translateX, translateY))
    }

    override fun rotate(angle: Double): IFigure {
        return create( figure.rotate(angle))
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return create(figure.rotate(angle, rotateCenter))
    }

    override fun collection(): List<IFigure> {
        return listOf(figure)
    }

    override fun name(): String {
        return this.javaClass.name
    }
}