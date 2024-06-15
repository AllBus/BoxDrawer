package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2


interface IFigure {

    val count: Int
    fun crop(k: Double, cropSide: CropSide): IFigure
    fun list(): List<Figure>
    fun rect(): BoundingRectangle

    fun translate(translateX: Double, translateY: Double): IFigure
    fun rotate(angle: Double): IFigure
    fun rotate(angle: Double, rotateCenter: Vec2): IFigure
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

class FigureInfo(
    val figure: IFigure,
    val parent: FigureInfo?,
    val transform: Matrix,
) {
    fun rect() = figure.rect().transform(transform)
}

enum class CropSide {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
}

abstract class Figure : IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return listOf(this)
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String {
        return this.javaClass.name
    }

    override val transform: Matrix
        get() = Matrix.identity

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureTranslate(this, Vec2(translateX, translateY))
    }

    override fun rotate(angle: Double): IFigure {
        return FigureRotate(this, angle, Vec2.Zero)
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureRotate(this, angle, rotateCenter)
    }

    companion object {
        val Empty = FigureEmpty
    }
}

interface Approximation {
    fun approximate(pointCount: Int): List<List<Vec2>>
}

object FigureEmpty : IFigure, IFigurePath {
    override fun positionInPath(delta: Double): PointWithNormal {
        return PointWithNormal.EMPTY
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        return PointWithNormal.EMPTY
    }

    override fun pathLength(): Double {
        return 0.0
    }

    override fun pathLength(edge: Int): Double {
        return 0.0
    }

    override fun edgeCount(): Int {
        return 0
    }

    override fun path(edge: Int): IFigurePath {
        return this
    }

    override fun startPoint(): Vec2 {
        return Vec2.Zero
    }

    override fun endPoint(): Vec2 {
        return Vec2.Zero
    }

    override fun take(startMM: Double, endMM: Double): IFigurePath {
        return this
    }

    override fun duplicationAtNormal(h: Double): IFigurePath {
        return this
    }

    override val count: Int
        get() = 0

    override fun crop(k: Double, cropSide: CropSide): IFigure = this

    override fun list(): List<Figure> = emptyList()

    override fun rect(): BoundingRectangle = BoundingRectangle.Empty

    override fun translate(translateX: Double, translateY: Double): IFigure = this

    override fun rotate(angle: Double): IFigure = this

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure = this

    override fun draw(g: IFigureGraphics) {}
    override fun print(): String {
        return ""
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String = "Empty"

    override val transform: Matrix
        get() = Matrix.identity
}