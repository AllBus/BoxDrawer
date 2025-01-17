package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Matrix

interface IFigure {

    val count: Int
    //fun list(): List<Figure>
    fun rect(): BoundingRectangle

    fun draw(g: IFigureGraphics)

    fun print(): String

    /** Список фигур внутри этой фигуры */
    fun collection(): List<IFigure>
    fun name(): String
    fun removeInner(inner: IFigure): IFigure
    fun replaceInner(newCollection: List<IFigure>): IFigure

    val transform: Matrix
    val hasTransform: Boolean

    companion object {
        fun list(figure: IFigure): List<IFigure> {
            return listOf(figure) + figure.collection().flatMap { list(it) }
        }

        fun path(figure: IFigure): List<IFigurePath> {
            val mt = Matrix()
            mt *= figure.transform
            return path(figure, mt)
        }

        fun path(figure: IFigure, matrix: Matrix): List<IFigurePath> {
            val nm = if (figure.hasTransform) matrix.copyWithTransform(figure.transform) else matrix

            val l: List<IFigurePath> = if (figure is IFigurePath) {
                if (nm.isIdentity())
                    listOf(figure)
                else {
                    listOf(figure.transform(nm))
                }
            } else
                emptyList()

            return l + figure.collection().flatMap { f -> path(f, nm) }
        }

        fun approximation(figure: IFigure, matrix: Matrix = Matrix()): List<Approximation> {
            val nm = matrix.copyWithTransform(figure.transform)

            val l: List<Approximation> = if (figure is Approximation) {
                if (nm.isIdentity())
                    listOf(figure)
                else {
                    //todo сдвинуть
                    listOf( figure)
                }
            } else
                emptyList()

            return l + figure.collection().flatMap { f -> approximation(f, nm) }
        }

    }
}

interface ICropable: IFigure{
    fun crop(k: Double, cropSide: CropSide): ICropable
}