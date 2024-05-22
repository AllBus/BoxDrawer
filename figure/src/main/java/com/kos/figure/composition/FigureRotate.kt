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
        return figure.rect().translate(pivot)
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureTranslate(this,
            Vec2(translateX, translateY)
        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureRotate(
            figure.rotate(angle),
            this.angle,
            pivot
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureRotate(
            figure.rotate(angle, rotateCenter),
            this.angle,
            pivot
        )
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
}