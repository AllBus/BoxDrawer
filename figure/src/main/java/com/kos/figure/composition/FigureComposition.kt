package com.kos.figure.composition

import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

abstract class FigureComposition : IFigure {

    abstract val figure: IFigure

    abstract fun create(figure: IFigure): FigureComposition

    override val count: Int
        get() = figure.count

    override fun rect(): BoundingRectangle {
        return figure.rect()
    }

    override fun collection(): List<IFigure> {
        return listOf(figure)
    }

    override fun name(): String {
        return this.javaClass.name
    }

    override val transform: Matrix
        get() = Matrix.identity
}