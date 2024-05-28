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
            return Empty
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
        if (points.size>=2){
            if (0 <= edge && edge < points.size-1){
                val pred = points[edge]
                val next = points[edge+1]
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
        return length.lastOrNull()?:0.0
    }

    override fun pathLength(edge:Int): Double {
        if (points.size>=2){
            if (0 <= edge && edge < points.size-1){
                val pred = points[edge]
                val next = points[edge+1]
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

    override fun edgeCount(): Int = points.size-1

    fun isClose(): Boolean {
        return if (points.size > 3) points.first() == points.last() else false
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        return listOf(points)
    }

    override fun path(edge: Int): IFigure {
        if (0 <= edge && edge < points.size-1){
            val pred = points[edge]
            val next = points[edge+1]
            return FigureLine(pred, next)
        }

        return FigureEmpty
    }

    override fun duplicationAtNormal(h: Double): IFigure {
        val pp = points.zipWithNext().flatMap { (pred, next) ->
            val n = Vec2.normal(pred, next)
            listOf(pred+n*h, next+n*h)
        }
        return FigurePolyline(pp)
    }
}