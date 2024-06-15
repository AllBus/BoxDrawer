package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureOnPath(
    val figure: IFigure,
    val path: IFigurePath,
    override val count: Int,
    val distanceInPercent: Double,
    val startOffsetInPercent: Double,
    val reverse: Boolean,
    val useNormal: Boolean,
    val angle: Double,
    var pivot: Vec2,
) : IFigure {

    override fun list(): List<Figure> {
        return figure.list()
    }

    override fun rect(): BoundingRectangle {
        val fr = figure.rect()
        val pr = figure.rect()// path.rect()

        return BoundingRectangle(
            pr.min - fr.min,
            pr.max + fr.max,
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        for (i in 0..<count) {
            val d = startOffsetInPercent + distanceInPercent*i
            val pos = if (reverse) (1 - d) else d

            val p = path.positionInPath(pos)
            g.save()
            g.translate(p.point.x, p.point.y)
         //   g.drawLine(Vec2.Zero, p.normal*20.0)
            if (useNormal) {
                g.rotate(p.normal.angle*180.0/Math.PI, Vec2.Zero)
            }
            g.translate(pivot.x, pivot.y)
            g.rotate(angle, Vec2.Zero)
            figure.draw(g)
            g.restore()
        }
        g.restore()
    }

    override fun print(): String {
        return "P"
    }

    override fun collection(): List<IFigure> {
        return listOf(figure)
    }

    override fun name(): String = "OnPath"

    override val transform: Matrix
        get() = Matrix.identity
}