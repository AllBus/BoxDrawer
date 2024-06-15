package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Approximation
import com.kos.figure.IFigure
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import vectors.BoundingRectangle

class FigureTranslateWithRotate(
    override val figure: IFigure,
    val offset: Vec2,
    val angleInDegrees: Double,
) : FigureComposition(), Approximation {

    override fun create(figure: IFigure): FigureComposition {
        return FigureTranslateWithRotate(figure, offset, angleInDegrees)
    }

    override fun rect(): BoundingRectangle {
        return figure.rect().translate(offset)
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureTranslateWithRotate(
            figure,
            offset + Vec2(translateX, translateY),
            angleInDegrees
        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureTranslateWithRotate(
            figure,
            offset,
            this.angleInDegrees + angle
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureTranslateWithRotate(
            figure.rotate(angle, rotateCenter),
            offset,
            angle
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.translate(offset.x, offset.y)
        g.rotate(angleInDegrees, Vec2.Zero)
        figure.draw(g)
        g.restore()
    }

    override fun print(): String {
        return "T (R(${figure.print()}))"
    }

    override fun name(): String {
        return "Сдвиг и вращение $offset $angleInDegrees"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return if (figure is Approximation)
            figure.approximate(pointCount).map { v -> v.map { (it.rotate(angleInDegrees*PI/ 180) + offset) } }
        else
            emptyList()
    }

    override val transform: Matrix
        get() {
            val m = Matrix.translate(offset.x, offset.y)
            m.rotateZ(angleInDegrees.toFloat())
            return m
        }
}

