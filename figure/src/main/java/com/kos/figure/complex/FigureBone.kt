package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.IFigure
import com.kos.figure.segments.model.BoneAnchor
import vectors.BoundingRectangle
import vectors.Matrix

class FigureBone(
    val figure: IFigure,
    val boneName:String,
    val dots: List<BoneAnchor>,
    val dotSize: Double = 1.0,
) : IFigure{
    override val count: Int
        get() = 1

    override fun rect(): BoundingRectangle {
        return figure.rect()
    }

    override fun draw(g: IFigureGraphics) {
        figure.draw(g)
        val gc = g.getColor()
        g.setColor(0xff0000)
        dots.forEach { d ->
            g.drawCircle(d.coordinate, dotSize)
        }
        g.setColor(gc)
    }

    override fun print(): String {
        return "/bone ($boneName) (${figure.print()}) (${dots.joinToString(" "){ "(${it.coordinate.x} ${it.coordinate.y} ${it.name})"}})"
    }

    override fun collection(): List<IFigure> {
        return listOf(figure)
    }

    override fun name(): String {
        return "bone"

    }

    override fun removeInner(inner: IFigure): IFigure {
        return this
    }

    override fun replaceInner(newCollection: List<IFigure>): IFigure {
        return this
    }

    override val transform: Matrix
        get() = Matrix.identity

    override val hasTransform: Boolean
        get() = false
}