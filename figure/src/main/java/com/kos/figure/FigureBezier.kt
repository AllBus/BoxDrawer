package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.algorithms.FigureBezierList
import com.kos.figure.complex.model.CurveList
import com.kos.figure.complex.model.PathIterator
import com.kos.figure.complex.model.SegmentList
import com.kos.figure.complex.model.SimpleElement
import vectors.Vec2
import vectors.Vec2.Companion.casteljauLine
import vectors.Vec2.Companion.getCubicRoots

private const val DEFAULT_STEP_SIZE = 1000

class FigureBezier(points: List<Vec2>) : FigurePolygon(points), FigureWithApproximation, SimpleElement {
    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureBezier(points)
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {

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
            return FigureEmpty
        return FigureBezierList(figures.toList()).toFigure()
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
                PointWithNormal.from(pred,pred, last)
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
                    PointWithNormal.from(last, pred, last)
                } else
                    PointWithNormal.EMPTY
            } else {
                val i = l.indexOfLast { it <= fp }

                val pd = if (i >= 0) l[i] else 0.0

                val pde = (l.getOrNull(i + 1) ?: fl)
                val c = (fp - pd) / (pde - pd)

               val pt =  points.windowed(4, 3).getOrNull(i+1)?.let { p ->
                    Vec2.bezierPosition(p, c, DEFAULT_STEP_SIZE,(pde - pd))
                } ?: PointWithNormal.EMPTY
                pt
            }
        } else
            if (points.size >= 2) {
                val last = points[1]
                val pred = points.first()
                PointWithNormal.from(pred, pred, last)
            } else
                PointWithNormal.EMPTY
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        if (edge< 0)
            return PointWithNormal.EMPTY

        val start = edge*3
        if (start+4<= points.size) {
            val p = points.subList(start, start + 4)
            val le = if (edge == 0 ) length[0] else (length[edge] - length[edge-1])
            return Vec2.bezierPosition(p, delta, DEFAULT_STEP_SIZE, le)
        }
        return PointWithNormal.EMPTY
    }

    override fun positionInPathAtMM(edge: Int, mm: Double): PointWithNormal {
        if (edge< 0)
            return PointWithNormal.EMPTY

        val start = edge*3
        if (start+4<= points.size) {
            val p = points.subList(start, start + 4)
            val le = if (edge == 0 ) length[0] else (length[edge] - length[edge-1])
            return Vec2.bezierPositionAtMM(p, mm, DEFAULT_STEP_SIZE, le)
        }
        return PointWithNormal.EMPTY
    }

    override fun pathLength(): Double {
        return length.lastOrNull()?:0.0
    }

    fun calculateLength(): List<Double> {
        var sum = 0.0
        return points.windowed(4, 3) { p ->
            sum += Vec2.bezierLength(p)
            sum
        }
    }

    override fun name(): String {
        return "Кривая ${this.points.size/3}"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return if (points.size>=4) {
            val result = ArrayList<Vec2>()
            result.add(points[0])

            points.windowed(4, 3).forEach { curve ->
                (1..pointCount).mapTo(result) { p ->
                    Vec2.bezierLerp(curve, p.toDouble() / pointCount)
                }
            }
            listOf(result)
        } else emptyList()
    }

    override fun edgeCount() = points.size/3

    override fun pathLength(edge: Int): Double {
        return (length.getOrNull(edge)?:0.0) - (length.getOrNull(edge-1)?:0.0)
    }

    override fun path(edge: Int): IFigurePath {
        if (edge>=0 && edge*3+4<= points.size) {
            val p = points.subList(edge * 3, edge * 3 + 4)
            return FigureBezier(p)
        }

        return FigureEmpty
    }

    override fun duplicationAtNormal(h: Double): Figure {
        val eps = 0.001
        val l =  points.windowed(4, 3).map { curve ->

            val sp = Vec2.bezierPosition(curve, 0.0, eps)
            val en = Vec2.bezierPosition(curve, 1.0, eps)
           
          //  val ns = Vec2.normal(curve[0], curve[1])
         //   val ne = Vec2.normal(curve[2], curve[3])
            listOf(
                curve[0]+sp.normal*h,
                curve[1]+sp.normal*h,
                curve[2]+en.normal*h,
                curve[3]+en.normal*h
            )
        }
        return FigureBezierList(l).toFigure()
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val pe = pathLength()
        if (pe<=0.0)
            return FigureEmpty

        if (points.size == 4) {
            val a = (startMM / pe).coerceIn(0.0, 1.0)
            val b = (endMM / pe).coerceIn(0.0, 1.0)

            val sec = Vec2.casteljauLine(points.subList(0, 4), a, b)
            return FigureBezier(sec)
        }else {
            val e1 = edgeAtPosition(startMM)
            val e2 = edgeAtPosition(endMM)

            val startB = if (e1>=0 && e1< edgeCount()) points.subList(0, (1+3*(e1))) else emptyList()
            val endB = if (e2>=0 && e2 < edgeCount()) points.subList(1+3*(e2), points.size) else emptyList()

            val a =if (e1>=0 && e1< edgeCount()){
                val st = startMM-length[e1-1] /pathLength(e1)
                 Vec2.casteljauLine(points.subList(3*(e1), 3*(e1)+4 ),st ).first
            } else emptyList()

            val b = if (e2>=0 && e2 < edgeCount()) {
                val en = endMM - length[e2 - 1] / pathLength(e2)
                Vec2.casteljauLine(points.subList(3 * (e2), 3 * (e2) + 4), en).second
            } else emptyList()

            val l = listOf(
                startB,
                a,
                b,
                endB)
            return FigureBezierList(l).toFigure()
        }
    }

    fun edgeAtPosition(mm:Double):Int{
        if (mm<0) return -1
        if (mm > pathLength())
            return edgeCount()+1
        return length.indexOfLast { it <= mm }
    }

    override fun segments(): PathIterator {
        return CurveList(points)
    }
}

