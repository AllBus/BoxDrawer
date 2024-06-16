package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Approximation
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

/**
 * @param angle degrees
 */
class FigureRotate(
    override val figure: IFigure,
    val angle: Double,
    val pivot: Vec2,
): FigureComposition(), Approximation {

    override fun create(figure: IFigure): FigureComposition {
        return FigureRotate(figure, angle, pivot)
    }

    override fun rect(): BoundingRectangle {
        return figure.rect()
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.rotate(angle,  pivot)
        figure.draw(g)
        g.restore()
    }

    override fun print(): String {
        return "R (${figure.print()})"
    }

    override fun name(): String {
        return "Поворот(${angle})"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return if (figure is Approximation)
            figure.approximate(pointCount).map { v -> v.map { it.rotate( angle, pivot) } }
        else
            emptyList()
    }

    override val transform: Matrix
        get() {
            val m = Matrix.translate(pivot.x, pivot.y)
            m.rotateZ(angle.toFloat())
            m.translate(-pivot.x.toFloat(), -pivot.y.toFloat())
            return m
        }

    override val hasTransform: Boolean
        get() = true
}