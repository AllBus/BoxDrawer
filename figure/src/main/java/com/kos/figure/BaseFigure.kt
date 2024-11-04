package com.kos.figure

import vectors.Matrix

abstract class BaseFigure : IFigure {
    override val count: Int
        get() = 1

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun removeInner(inner: IFigure): IFigure  = this

    override fun replaceInner(newCollection: List<IFigure>): IFigure = this
}