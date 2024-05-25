package com.kos.boxdrawer.detal.soft

import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureRez
import vectors.Vec2
import kotlin.math.abs


class SoftRez {

    fun drawRez(
        widthF: Double,
        heightF: Double,
        delta: Double,
        dlina: Double,
        soedinenie: Double,
        firstSmall: Boolean
    ): IFigure {

        if (delta< 0.1)
            return FigureEmpty

        val width = abs(widthF)
        val height = abs(heightF)

        val coord = Vec2(if (widthF<0) -width else 0.0 , if (heightF<0) -height else 0.0)

        val count = (width/delta).toInt()

        if (count<=0|| count > 1000)
            return FigureEmpty
        val dn = width/ count
        val countw = ((height-soedinenie) / (dlina + soedinenie)).toInt()
        if (countw<=0 || countw > 1000)
            return FigureEmpty
        val dw =  (height-soedinenie)/ countw
        return FigureRez(coord, count, countw, dn, dw-soedinenie, soedinenie, firstSmall)
    }

    fun drawRectangle(
        w: Double, h: Double,
    ):IFigure{
         return FigurePolyline(
                listOf(
                    Vec2(0.0, 0.0),
                    Vec2(w, 0.0),
                    Vec2(w, h),
                    Vec2(0.0, h),
                    Vec2(0.0, 0.0),
                )
            )
    }

    fun drawRect(
        w: Double, h: Double, sdx: Double, sdy: Double, xCount: Int, yCount: Int, fit: Boolean, form: IFigure,
    ): IFigure {


        val res = mutableListOf<IFigure>()
//
//        res.add(
//            FigurePolyline(
//                listOf(
//                    Vec2(0.0, 0.0),
//                    Vec2(w, 0.0),
//                    Vec2(w, h),
//                    Vec2(0.0, h),
//                    Vec2(0.0, 0.0),
//                )
//            )
//        )

        if (xCount <= 0 || (yCount <= 0 && !fit))
            return Figure.Empty

        if (form.count == 0)
            return Figure.Empty

        val rr = form.rect()

        val sww = (w - sdx) / xCount
        val sw = sww - sdx
        val scaleX = if (rr.width <= 1) 1.0 else sw / rr.width

        var sh = 0.0
        var scaleY = 1.0

        if (sw <= 0) return Figure.Empty

        if (fit) {
            scaleY = scaleX
            if (rr.width == 0.0) {
                return Figure.Empty
            }
            sh = sw * rr.height / rr.width
        } else {
            val shh = h / yCount
            sh = shh - 2 * sdy
            scaleY = if (rr.height <= 1) 1.0 else sh / rr.height
        }

        if (sh < 0.001 && sdy < 0.001) return Figure.Empty

        val trX = -rr.min.x
        val trY = -rr.min.y

        var j = 0

        val fa = form.translate(trX, trY)

        val dy = (sh * 0.5) + sdy

        if (sww < 0.01 || dy < 0.01)
            return Figure.Empty

        var y = -0.5 * sh
        while (y < h) {
            val fy = when {
                y < sdy -> fa.crop(-y / scaleY, CropSide.BOTTOM);
                y + sh > h -> fa.crop((h - y) / scaleY, CropSide.TOP);
                else -> fa
            }

            j++

            val x1 = sdx + (j % 2) * -0.5 * (sww)

            val (ff1, xs) = if (x1<sdx) {
                Pair(
                    fy.crop((-x1) / scaleX, CropSide.LEFT),
                    x1 + sww
                )
            } else
                Pair(null,x1)

            // количество целых элементов в линии
            val sx = ((w-xs)/sww ).toInt()
            val x2 = xs+sx*sww

            val ffe =
            if (x2+sw > w){
                fy.crop((w - x2) / scaleX, CropSide.RIGHT)
            } else
                fy

            if (sx>0){
                res.add(
                    FigureArray(
                    fy, Vec2(x1, y),
                    distance = Vec2(sww, 0.0),
                    columns = sx,
                    rows = 1,
                    angle = 0.0,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    figureStart = ff1,
                    figureEnd = ffe
                )
                )
            }

            y += dy
        }

        return FigureList(res.toList())
    }

}