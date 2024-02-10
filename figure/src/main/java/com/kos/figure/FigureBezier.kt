package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.Vec2
import vectors.Vec2.Companion.casteljauLine
import vectors.Vec2.Companion.getCubicRoots

class FigureBezier(points: List<Vec2>) : FigurePolygon(points) {
    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureBezier(points)
    }

    override fun crop(k: Double, cropSide: CropSide): IFigure {

        val figures = mutableListOf<List<Vec2>>()

        val (predicate, napr) = when (cropSide) {
            CropSide.LEFT ->
                Pair({ a: Vec2 -> a.x >= k }, { a: Vec2 -> a.x })

            CropSide.RIGHT ->
                Pair({ a: Vec2 -> a.x <= k }, { a: Vec2 -> a.x })

            CropSide.BOTTOM ->
                Pair({ a: Vec2 -> a.y >= k }, { a: Vec2 -> a.y })

            CropSide.TOP ->
                Pair({ a: Vec2 -> a.y <= k }, { a: Vec2 -> a.y })
        }//end when

        if (points.all(predicate))
            return this

        for (d in 1..points.size - 3 step 3) {
            val ps = points.subList(d - 1, d + 3)
            when {
                ps.all(predicate) -> figures.add(ps)
                !ps.all { !predicate(it) } -> {
                    val roots = getCubicRoots(ps.map(napr).map { it - k })
                    //Todo: Правильно фильтровать
                    figures.addAll(casteljauLine(ps, roots).filter { l -> predicate(l[1]) })
                }
            }
        }

        if (figures.isEmpty())
            return Empty
        return FigureBezierList(figures.toList())
    }

    override fun draw(g: IFigureGraphics) {
        if (points.size>=4) {
            g.drawBezier(points)
        }
    }
}

