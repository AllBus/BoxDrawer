package com.kos.boxdrawer.detal.soft

import com.kos.boxdrawe.drawer.IFigureGraphics
import figure.CropSide
import figure.IFigure
import vectors.Vec2


class SoftRez {

    fun drawRect(
        g: IFigureGraphics,
        w: Double, h: Double, sdx: Double, sdy: Double, xCount: Int, yCount: Int, fit: Boolean, form: IFigure,
    ) {

        g.drawRect(Vec2(0.0, 0.0), Vec2(w, h))

        if (xCount <= 0 || (yCount <= 0 && !fit))
            return

        if (form.count == 0)
            return

        val rr = form.rect()

        val sww = (w - sdx) / xCount
        val sw = sww - sdx
        val scaleX = if (rr.width <= 1) 1.0 else sw / rr.width

        var sh = 0.0
        var scaleY = 1.0

        if (sw <= 0) return

        if (fit) {
            scaleY = scaleX
            if (rr.width == 0.0) {
                return
            }
            sh = sw * rr.height / rr.width
        } else {
            val shh = h / yCount
            sh = shh - 2 * sdy
            scaleY = if (rr.height <= 1) 1.0 else sh / rr.height
        }

        if (sh < 0.001 && sdy < 0.001) return

        val trX = -rr.min.x
        val trY = -rr.min.y

        var j = 0

        val fa = form.translate(trX, trY)

        val dy = (sh * 0.5) + sdy

        if (sww<0.01 || dy< 0.01)
            return

        var y = -0.5 * sh
        while (y < h) {
            val fy = when {
                y < sdy -> fa.crop(-y / scaleY, CropSide.BOTTOM);
                y + sh > h -> fa.crop((h - y) / scaleY, CropSide.TOP);
                else -> fa
            }

            j++;

            var x = sdx + (j % 2) * -0.5 * (sww)
            while (x < w) {
                val f = when {
                    x < sdx -> fy.crop((-x) / scaleX, CropSide.LEFT);
                    x + sw > w -> fy.crop((w - x) / scaleX, CropSide.RIGHT);
                    else -> fy
                }

                val a = createSoft(g, x, y, scaleX, scaleY, f);
                x += sww
            }

            y += dy
        }
    }

    private fun createSoft(
        g: IFigureGraphics,
        x: Double,
        y: Double,
        scaleX: Double,
        scaleY: Double,
        form: IFigure
    ) {
        g.save()
        g.translate(x, y)
        g.scale(scaleX, scaleY)
        form.draw(g)
        g.load()
    }
}