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

    abstract fun translate(translateX: Double, translateY: Double): Figure

    abstract fun rotate(angle: Double): Figure

    abstract fun rotate(angle: Double, rotateCenter: Vec2): Figure

    abstract fun transform(matrix: Matrix): Figure

    abstract fun crop(k: Double, cropSide: CropSide): Figure
}

interface Approximation {
    fun approximate(pointCount: Int): List<List<Vec2>>
}

interface FigureWithApproximation: IFigure,  Approximation

object FigureEmpty : Figure(), IFigurePath {
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

    override fun list(): List<Figure> = emptyList()

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