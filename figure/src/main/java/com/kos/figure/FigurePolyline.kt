package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.Vec2
import vectors.Vec2.Companion.coordForX
import vectors.Vec2.Companion.coordForY

class FigurePolyline(points: List<Vec2>) : FigurePolygon(points), Approximation {

    constructor(points: List<Vec2>, close: Boolean) : this(
        if (close) (points + listOfNotNull(points.firstOrNull()))
        else points
    )

    override fun create(points: List<Vec2>): FigurePolygon {
        return FigurePolyline(points)
    }


    override fun crop(k: Double, cropSide: CropSide): IFigure {
        if (points.size < 2) {
            return FigureEmpty
        }
        return cropPolyline(k, cropSide);
    }


    fun cropPolyline(k: Double, cropSide: CropSide): IFigure {
        val figures = mutableListOf<IFigure>()
        var result = mutableListOf<Vec2>()

        fun saveFigure() {
            if (result.size >= 2) {
                figures.add(
                    if (result.size == 2)
                        FigureLine(result[0], result[1])
                    else
                        FigurePolyline(result.toList())
                )
            }
            result = mutableListOf<Vec2>()
        }

        fun crops(predicate: (Vec2, Double) -> Boolean, coord: (Vec2, Vec2, Double) -> Vec2) {
            var a = points.first();
            var predV = predicate(a, k)

            for (b in points) {
                if (predicate(b, k)) {
                    if (!predV) {
                        result.add(coord(a, b, k))
                    }
                    result.add(b);
                    predV = true
                } else {
                    if (predV) {
                        result.add(coord(a, b, k))
                        saveFigure()
                        predV = false
                    }
                }
                a = b;
            }
            saveFigure()
        }

        when (cropSide) {
            CropSide.LEFT ->
                crops({ a, x -> a.x > x }, ::coordForX)

            CropSide.RIGHT ->
                crops({ a, x -> a.x < x }, ::coordForX)

            CropSide.BOTTOM ->
                crops({ a, y -> a.y > y }, ::coordForY)

            CropSide.TOP ->
                crops({ a, y -> a.y < y }, ::coordForY)
        }//end when


        return if (figures.size == 1) {
            figures.first()
        } else {
            FigureList(figures.toList())
        }
    }

    override fun draw(g: IFigureGraphics) {
        if (points.size >= 2) {
            g.drawPolyline(points)
        }
    }

    override fun print(): String {
        return "M 0 0 L " + points.map { p -> "${p.x} ${p.y}" }.joinToString(" ")
    }

    private val length: List<Double> by lazy { calculateLength() }

    override fun positionInPath(delta: Double): PointWithNormal {
        val l = length
        return if (l.isNotEmpty()) {
            val fl = l.last()
            val fp = fl * delta
            if (fp >= fl) {
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

                val pred = points[i + 1]
                val next = points[i + 2]
                val p = Vec2.lerp(pred, next, c)
                PointWithNormal.from(p, pred, next)
            }
        } else
            if (points.size >= 2) {
                val last = points[1]
                val pred = points.first()
                PointWithNormal.from(pred, last)
            } else
                PointWithNormal.EMPTY
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        if (points.size >= 2) {
            if (0 <= edge && edge < points.size - 1) {
                val pred = points[edge]
                val next = points[edge + 1]
                val p = Vec2.lerp(pred, next, delta)
                return PointWithNormal.from(p, pred, next)
            }
        }
        return PointWithNormal.EMPTY
    }

    fun calculateLength(): List<Double> {
        val iterator = points.iterator()
        if (!iterator.hasNext()) return emptyList()
        val result = mutableListOf<Double>()
        var sum = 0.0
        var current = iterator.next()
        while (iterator.hasNext()) {
            val next = iterator.next()
            sum += Vec2.distance(current, next)
            result.add(sum)
            current = next
        }
        return result
    }

    override fun pathLength(): Double {
        return length.lastOrNull() ?: 0.0
    }

    override fun pathLength(edge: Int): Double {
        if (points.size >= 2) {
            if (0 <= edge && edge < points.size - 1) {
                val pred = points[edge]
                val next = points[edge + 1]
                val p = Vec2.distance(pred, next)
                return p
            }
        }
        return 0.0
    }

    override fun name(): String {
        if (points.isEmpty()) return "Пустой Многоугольник"
        if (points.size == 2) return "Отрезок"
        return if (points.first() == points.last())
            "Многоугольник ${points.size - 1}"
        else
            "Полилиния ${points.size}"
    }

    override fun edgeCount(): Int = points.size - 1

    fun isClose(): Boolean {
        return if (points.size > 3) points.first() == points.last() else false
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return listOf(points)
    }

    override fun path(edge: Int): IFigurePath {
        if (0 <= edge && edge < points.size - 1) {
            val pred = points[edge]
            val next = points[edge + 1]
            return FigureLine(pred, next)
        }

        return FigureEmpty
    }

    override fun duplicationAtNormal(h: Double): IFigure {
        if (points.size == 2) {
            val pred = points[0]
            val next = points[1]
            val n = Vec2.normal(pred, next)
            return FigureLine(
                pred + n * h, next + n * h
            )
        } else
            if (points.size >= 3) {
                var pred = points[0]
                var next = points[1]
                val res = mutableListOf<Vec2>()
                if (isClose()) {
                    pred = points[points.size - 2]
                    next = points[0]
                    val n = Vec2.normal(pred, next)
                    val a = pred + n * h
                    val b = next + n * h
                    pred = points[0]
                    next = points[1]
                    val n2 = Vec2.normal(pred, next)
                    val c = pred + n2 * h
                    val d = next + n2 * h
                    Vec2.intersection(a, b, c, d)?.let { ins ->
                        res.add(ins)
                    } ?: res.add(pred + n * h)
                } else {
                    pred = points[0]
                    next = points[1]
                    val n = Vec2.normal(pred, next)
                    res.add(pred + n * h)
                }

                for (i in 1 until points.size - 1) {
                    val start = pred
                    pred = points[i + 0]
                    next = points[i + 1]

                    val n = Vec2.normal(start, pred)
                    val a = start + n * h
                    val b = pred + n * h

                    val n2 = Vec2.normal(pred, next)
                    val c = pred + n2 * h
                    val d = next + n2 * h
                    Vec2.intersection(a, b, c, d)?.let { ins ->
                        res.add(ins)
                    }
                }
                if (isClose()) {
                    res.add(res.first())
                } else {
                    val n2 = Vec2.normal(pred, next)
                    val d = next + n2 * h
                    res.add(d)
                }
                return FigurePolyline(res)
            } else
                return FigureEmpty
    }

    override fun take(startMM: Double, endMM: Double): IFigure {
        val e1 = edgeAtPosition(startMM)
        val e2 = edgeAtPosition(endMM)


        val a1 = if (e1 >= 0 && e1 < edgeCount()) {
            val a = points[e1]
            val b = points[e1 + 1]
            val d = Vec2.distance(a, b)
            if (d <= 0.0)
                points.subList(0, e1)
            else
                points.subList(0, e1) + Vec2.lerp(a, b, startMM / d)
        } else emptyList()

        val a2 = if (e2 >= 0 && e2 < edgeCount()) {
            val a = points[e2]
            val b = points[e2 + 1]
            val d = Vec2.distance(a, b)
            if (d <= 0.0)
                points.drop(e2)
            else
                listOf(Vec2.lerp(a, b, endMM / d)) + points.drop(e2)

        } else emptyList()

        return FigureList(
            listOfNotNull(
                if (a1.isNotEmpty()) FigurePolyline(a1) else null,
                if (a2.isNotEmpty()) FigurePolyline(a2) else null
            )
        )
    }

    fun edgeAtPosition(mm: Double): Int {
        if (mm < 0) return -1
        if (mm > pathLength())
            return edgeCount() + 1
        return length.indexOfLast { it <= mm }
    }
}