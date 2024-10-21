package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

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

abstract class Figure : IFigure, ICropable {
    override val count: Int
        get() = 1

    override fun collection(): List<IFigure> = emptyList()

    override val transform: Matrix
        get() = Matrix.identity

    override val hasTransform: Boolean
        get() = false

    abstract fun translate(translateX: Double, translateY: Double): Figure

    abstract fun transform(matrix: Matrix): Figure

    abstract override fun crop(k: Double, cropSide: CropSide): Figure
}

interface Approximation {
    fun approximate(pointCount: Int): List<List<Vec2>>
}

interface FigureWithApproximation: IFigure,  Approximation

object FigureEmpty : Figure(), IFigurePath, IRotable {
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

    override fun take(startMM: Double, endMM: Double) = this

    override fun duplicationAtNormal(h: Double) = this

    override val count: Int
        get() = 0

    override fun crop(k: Double, cropSide: CropSide): Figure = this

    override fun rect(): BoundingRectangle = BoundingRectangle.Empty

    override fun translate(translateX: Double, translateY: Double) = this

    override fun rotate(angle: Double) = this

    override fun rotate(angle: Double, rotateCenter: Vec2) = this

    override fun transform(matrix: Matrix) = this

    override fun toFigure(): Figure = this

    override fun draw(g: IFigureGraphics) {}
    override fun print(): String {
        return ""
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String = "Empty"

    override val transform: Matrix
        get() = Matrix.identity
}