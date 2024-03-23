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
        if (points.size >= 4) {
            g.drawBezier(points)
        }
    }

    override fun print(): String {
        var st = points.first()
        return "M ${st.x} ${st.y} b ${
            points.drop(1).flatMapIndexed { i, v ->
                val r = listOf(v.x - st.x, v.y - st.y)
                if ((i + 1) % 3 == 0) st = v
                r
            }.joinToString(" ")
        }"
    }

    private val length: List<Double> by lazy { calculateLength() }

    override fun positionInPath(delta: Double): PointWithNormal {
        if (delta <= 0.0){
            return  if (points.size >= 2) {
                val last = points[1]
                val pred = points.first()
                PointWithNormal.from(pred, last)
            } else
                PointWithNormal.EMPTY
        }
        val l = length
        return if (l.isNotEmpty()) {
            val fl = l.last()
            val fp = fl * delta
            if (fp>=fl){
                if (points.size >= 2) {
                    val last = points.last()
                    val pred = points[points.size - 2]
                    PointWithNormal.fromPreviousPoint(last, pred)
                } else
                    PointWithNormal.EMPTY
            } else {
                val i = l.indexOfLast { it <= fp }

                val pd = if (i >= 0) l[i] else 0.0

                val pde = (l.getOrNull(i + 1) ?: fl)
                val c = (fp - pd) / (pde - pd)

               val pt =  points.windowed(4, 3).getOrNull(i+1)?.let { p ->
                    Vec2.bezierPosition(p, c, 1000  ,(pde - pd))
                } ?: PointWithNormal.EMPTY
                pt
            }
        } else
            if (points.size >= 2) {
                val last = points[1]
                val pred = points.first()
                PointWithNormal.from(pred, last)
            } else
                PointWithNormal.EMPTY
    }

    fun calculateLength(): List<Double> {
        var sum = 0.0
        return points.windowed(4, 3) { p ->
            sum += Vec2.bezierLength(p)
            sum
        }
    }
}

