package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Vec2

class FigureArray(
    val figure: IFigure,
    val startPoint: Vec2,
    val distance: Vec2,
    val columns: Int,
    val rows: Int,
    val angle: Double = 0.0,
    val scaleX: Double = 1.0,
    val scaleY: Double = 1.0,
    val figureStart: IFigure? = null,
    val figureEnd: IFigure? = null,
) : IFigure {
    override val count: Int
        get() = 1

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return this //FigureArray(figure.crop(k, cropSide), startPoint, distance, size)
    }

    override fun list(): List<Figure> {
        return figure.list() + figureStart?.list().orEmpty() + figureEnd?.list().orEmpty()
    }

    override fun rect(): BoundingRectangle {
        val r = figure.rect().scale(scaleX, scaleY)
        val u = if (figureStart == null) 0.0 else 1.0
        return BoundingRectangle.union(
            listOfNotNull(
                figureStart?.rect()?.scale(scaleX, scaleY)?.translate(startPoint),
                figureEnd?.rect()?.scale(scaleX, scaleY)
                    ?.translate(startPoint + distance * (columns.toDouble() + u)),
                r.translate(startPoint + distance * u),
                r.translate(startPoint + distance * (columns.toDouble() + u - 1)),
            )
        )
    }

    val endPoint get() = startPoint + distance * (columns + 1).toDouble()


    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureArray(
            figure, startPoint + Vec2(translateX, translateY),
            distance, columns, rows, angle,
            scaleX, scaleY, figureStart, figureEnd
        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureArray(
            figure, startPoint,
            distance, columns, rows, angle + this.angle,
            scaleX, scaleY, figureStart, figureEnd
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureArray(
            figure, startPoint,
            distance, columns, rows, angle + this.angle,
            scaleX, scaleY, figureStart, figureEnd
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        if (angle != 0.0) {
            g.rotate(angle, startPoint)
        }

        for (j in 1..rows) {
            g.save()
            g.translate(startPoint.x, startPoint.y)
            figureStart?.let { f ->
                g.save()
                g.scale(scaleX, scaleY)
                f.draw(g)
                g.restore()
                g.translate(distance.x, distance.y)
            }


            for (i in 1..columns) {
                g.save()
                g.scale(scaleX, scaleY)
                figure.draw(g)
                g.restore()
                g.translate(distance.x, 0.0)
            }

            figureEnd?.let { f ->
                g.save()
                g.scale(scaleX, scaleY)
                f.draw(g)
                g.restore()
            }

            g.restore()
            g.translate(0.0, distance.y)
        }
        g.restore()
    }
}