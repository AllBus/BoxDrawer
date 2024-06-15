package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Matrix

interface IFigure {

    val count: Int
    fun list(): List<Figure>
    fun rect(): BoundingRectangle

    fun draw(g: IFigureGraphics)

    fun print(): String

    /** Список фигур внутри этой фигуры */
    fun collection(): List<IFigure>
    fun name(): String

    val transform: Matrix

    companion object {
        fun list(figure: IFigure): List<IFigure> {
            return listOf(figure) + figure.collection().flatMap { list(it) }
        }

        fun tree(figure: IFigure): List<FigureInfo> {
            val mt = Matrix.identity
            mt *= figure.transform
            return tree(figure, null, mt)
        }

        fun tree(figure: IFigure, parent: FigureInfo?, matrix: Matrix): List<FigureInfo> {
            val nm = matrix.copyWithTransform(figure.transform)
            val tek = FigureInfo(figure, parent, matrix)
            return listOf(tek) +
                    figure.collection().flatMap { f ->
                        tree(f, tek, nm)
                    }
        }
    }
}