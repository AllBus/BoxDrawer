package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureRez(
    val startPoint: Vec2,
    val countx: Int,
    val county: Int,
    val delta: Double,
    val dlina: Double,
    val soedinenie: Double,
    val firstSmall: Boolean,
) :
    IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(
            Vec2(0.0, 0.0),
            Vec2(countx * delta, county * (dlina + soedinenie) + soedinenie)
        )
    }

    override fun draw(g: IFigureGraphics) {
        if (countx > 1000 || county > 1000)
            return

        val ws = (dlina + soedinenie) / 2
        val dsy = startPoint.y + ws + soedinenie
        val dpy = startPoint.y + soedinenie
        val sp = soedinenie + dlina
        val cou = county - 1

        val (xa, xb) = if (!firstSmall) {
            0.0 to delta / 2
        } else {
            delta / 2 to 0.0
        }

        for (x in 0 until countx) {
            val xp = startPoint.x + x * delta
            val xf = xp + xa
            val xe = xp + xb
            g.drawLine(Vec2(xf, startPoint.y), Vec2(xf, startPoint.y + ws))
            g.drawLine(Vec2(xf, dsy + cou * sp), Vec2(xf, dsy + cou * sp + ws))
            g.drawLine(
                Vec2(xe, dpy + cou * sp),
                Vec2(xe, dpy + cou * sp + dlina)
            )
            for (y in 0 until cou) {
                g.drawLine(Vec2(xf, dsy + y * sp), Vec2(xf, dsy + y * sp + dlina))
                g.drawLine(
                    Vec2(xe, dpy + y * sp),
                    Vec2(xe, dpy + y * sp + dlina)
                )
            }
        }

        if (!firstSmall) {
            val xp = startPoint.x + countx * delta
            g.drawLine(Vec2(xp, startPoint.y), Vec2(xp, startPoint.y + ws))
            g.drawLine(Vec2(xp, dsy + cou * sp), Vec2(xp, dsy + cou * sp + ws))
            for (y in 0 until cou) {
                g.drawLine(Vec2(xp, dsy + y * sp), Vec2(xp, dsy + y * sp + dlina))
            }
        } else {
            val xe = startPoint.x + countx * delta
            for (y in 0 until county) {
                g.drawLine(
                    Vec2(xe, dpy + y * sp),
                    Vec2(xe, dpy + y * sp + dlina)
                )
            }
        }
    }

    override fun print(): String {
        return "/rez ${countx * delta} ${county * (dlina + soedinenie) + soedinenie} ${delta} ${dlina} ${soedinenie}"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "Мягкий рез"
    }

    override val transform: Matrix
        get() = Matrix.identity

    override val hasTransform: Boolean
        get() = false
}
