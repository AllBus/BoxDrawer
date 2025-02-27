package com.kos.figure.editor

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Approximation
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureComposition
import com.kos.figure.composition.FigureTranslateWithRotate
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI

class FigureMutableTransform(override val figure:IFigure,
                             var offset:Vec2,
                             var angleInDegrees:Double
)
    : FigureComposition(), Approximation {

    override fun create(figure: IFigure): FigureComposition {
        return FigureTranslateWithRotate(figure, offset, angleInDegrees)
    }

    override fun rect(): BoundingRectangle {
        return figure.rect().translate(offset)
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

    override val hasTransform: Boolean
        get() = true

    override fun removeInner(inner: IFigure): IFigure {
        if (inner === figure)
            return FigureEmpty
        return FigureTranslateWithRotate(figure.removeInner(inner), offset, angleInDegrees)
    }

    override fun replaceInner(newCollection: List<IFigure>): IFigure {
        return if (newCollection.isEmpty())
            FigureEmpty
        else
            FigureTranslateWithRotate(newCollection[0], offset, angleInDegrees)
    }
}

