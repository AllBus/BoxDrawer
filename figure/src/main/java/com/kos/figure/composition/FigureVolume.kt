package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.IFigure
import com.kos.figure.complex.model.VolumePosition
import vectors.BoundingRectangle
import vectors.Matrix

class FigureVolume(val figures: List< VolumePosition>): IFigure {
    override val count: Int
        get() = 1

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.Empty
    }

    override fun draw(g: IFigureGraphics) {

        figures.forEach { p ->
            g.save()

            g.transform(p.transform) {
                p.figure.draw(g)
            }
            g.restore()
        }
    }

    override fun print(): String {
        return "/volume (${figures.joinToString(" ") { p -> "[${p.figure.print()}] ${p.x} ${p.y} ${p.z} ${p.rotateX} ${p.rotateY} ${p.rotateZ}" }})"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "volume"
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