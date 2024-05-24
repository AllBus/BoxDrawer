package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureRez(val startPoint: Vec2, val countx: Int, val county: Int, val delta: Double,val  dlina: Double, val soedinenie: Double) :
    IFigure {
    override val count: Int
        get() = 1

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        //Todo
        return this
    }

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(Vec2(0.0, 0.0), Vec2(countx* delta, county*(dlina+soedinenie)+soedinenie))
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        //TODO
        return FigureRez(
            startPoint+ Vec2(translateX, translateY),
            countx, county, delta, dlina, soedinenie

        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureRotate(this, angle, Vec2.Zero)
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureRotate(this, angle, rotateCenter)
    }

    override fun draw(g: IFigureGraphics) {
        if (countx> 1000 || county > 1000)
            return


        val ws = (dlina+soedinenie)/2
        val dsy = ws+soedinenie
        val dpy = soedinenie
        val sp = soedinenie+dlina
        val cou =county-1
        for (x in 0 until countx) {
            val xp = startPoint.x+x*delta
            g.drawLine(Vec2(xp, 0.0), Vec2(xp, ws))
            g.drawLine(Vec2(xp, dsy+cou*sp), Vec2(xp, dsy+cou*sp+ws))
            g.drawLine(Vec2(xp+delta/2, dpy+cou*sp), Vec2(xp+delta/2, dpy+cou*sp+dlina))
            for (y in 0 until cou) {
                g.drawLine(Vec2(xp, dsy+y*sp), Vec2(xp, dsy+y*sp+dlina))
                g.drawLine(Vec2(xp+delta/2, dpy+y*sp), Vec2(xp+delta/2, dpy+y*sp+dlina))
            }
        }
        val xp = startPoint.x+countx*delta
        g.drawLine(Vec2(xp, 0.0), Vec2(xp, ws))
        g.drawLine(Vec2(xp, dsy+cou*sp), Vec2(xp, dsy+cou*sp+ws))
        for (y in 0 until cou) {
            g.drawLine(Vec2(xp, dsy + y * sp), Vec2(xp, dsy + y * sp + dlina))
        }
    }

    override fun print(): String {
        return "/rez ${countx* delta} ${county*(dlina+soedinenie)+soedinenie} ${delta} ${dlina} ${soedinenie}"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "Мягкий рез"
    }

    override val transform: Matrix
        get() = Matrix.identity

}
