package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.collections.FigurePath
import com.kos.figure.complex.model.SimpleElement
import com.kos.figure.segments.model.Arc
import com.kos.figure.segments.model.Curve
import com.kos.figure.segments.model.CustomPathIterator
import com.kos.figure.segments.model.Ellipse
import com.kos.figure.segments.model.PathElement
import com.kos.figure.segments.model.PathIterator
import com.kos.figure.segments.model.Segment
import vectors.PointWithNormal
import kotlin.math.max
import kotlin.math.min

open class FigureRound(
    val segments: List<PathElement>
) : Figure(), IFigurePath, SimpleElement {

    override val count: Int
        get() = 1

    protected val fullLength by lazy { segments.sumOf { it.perimeter() } }

    override fun positionInPath(delta: Double): PointWithNormal {
        //val le = delta*pathLength()
        var ostatok = delta * pathLength()
        if (ostatok < 0)
            return PointWithNormal.EMPTY
        for (s in segments) {
            val q = s.perimeter()
            if (q > 0) {
                if (ostatok <= q) {
                    return s.positionInPath(ostatok / q)
                } else {
                    ostatok -= q
                }
            }
        }
        return PointWithNormal.EMPTY
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        if (edge >= 0 && edge < segments.size) {
            val s = segments[edge]
            return s.positionInPath(delta)
        }
        return PointWithNormal.EMPTY
    }

    override fun pathLength(): Double {
        return fullLength
    }


    override fun pathLength(edge: Int): Double {
        if (edge >= 0 && edge < segments.size) {
            return segments[edge].perimeter()
        }
        return 0.0
    }

    override fun edgeCount(): Int {
        return segments.size
    }

    private fun toPath(segment: PathElement): IFigurePath {
        return when (segment){
//            is Segment -> segment.toPath()
//            is Arc -> segment.toPath()
//            is Curve -> segment.toPath()
           // is Ellipse -> segment.toPath()
            else -> FigureEmpty
        }
    }

    private fun take(segment: PathElement, start:Double, end: Double): Figure {
        return when (segment){
//            is Segment -> segment.take(start, end)
//            is Arc -> segment.take(start, end)
//            is Curve -> segment.take(start, end)
//            is Ellipse -> segment.take(start, end)
            else -> FigureEmpty
        }
    }

    private fun toFigure(segment: PathElement): Figure {
        return when (segment){
//            is Segment -> segment.toFigure()
//            is Arc -> segment.toFigure()
//            is Curve -> segment.toFigure()
//            is Ellipse -> segment.toFigure()
            else -> FigureEmpty
        }
    }


    override fun path(edge: Int): IFigurePath {
        if (edge >= 0 && edge < segments.size) {
            return toPath(segments[edge])
        }
        return FigureEmpty
    }

    override fun startPoint(): vectors.Vec2 {
        return if (segments.isNotEmpty())
            segments[0].start
        else
            vectors.Vec2.Zero
    }

    override fun endPoint(): vectors.Vec2 {
        return if (segments.isNotEmpty())
            segments.last().end
        else
            vectors.Vec2.Zero
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val le = pathLength()
        if (le <= 0)
            return FigureEmpty

        val st = startMM.coerceIn(0.0, le)
        val end = endMM.coerceIn(0.0, le)

        val result = mutableListOf<Figure>()
        var state = true
        var ostatok = st
        for (s in segments) {
            val q = s.perimeter()
            if (q > 0) {
                if (state) {
                    if (ostatok <= q) {
                        result.add(take(s, ostatok, q))
                        ostatok += end - st
                        state = false
                    }
                } else {
                    if (ostatok >= q) {
                        result.add(toFigure(s))
                    } else {
                        result.add(take(s, 0.0, ostatok))
                        break
                    }
                }
                ostatok -= q
            }
        }

        return FigurePath(result.toList())
    }

    override fun print(): String {
        return "/path ${
            segments.joinToString(" ") { e ->
                when (e) {
                    is Segment -> "(${e.start} ${e.end})"
                    is Arc -> "(${e.center} ${e.radius} ${e.startAngle} ${e.endAngle} ${e.outSide})"
                    is Curve -> "(${e.p0} ${e.p1} ${e.p2} ${e.p3})"
                    is Ellipse -> "(${e.center} ${vectors.Vec2(e.radiusX, e.radiusY)} ${e.startAngle} ${e.endAngle} ${e.outSide} ${e.rotation})"
                    else -> ""
                }
            }
        }}"
    }

    override fun name(): String {
        return "Round"
    }

    override fun translate(translateX: Double, translateY: Double): Figure {
        return FigureRound(
            segments = segments.map {
                it.translate(vectors.Vec2(translateX, translateY))
            }
        )
    }

    override fun toFigure(): Figure {
        return this
    }

    override fun transform(matrix: vectors.Matrix): FigurePath {
        return FigurePath(
            segments.map { toFigure(it) }.map { it.transform(matrix) }
        )
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return FigurePath(segments.map { toFigure(it) }.map { it.crop(k, cropSide) })
    }

    override fun draw(g: IFigureGraphics) {
        segments.forEach { s ->
            s.draw(g)
        }
    }

    override fun duplicationAtNormal(h: Double): Figure {
        // Todo: Нужно правильно вычислить пересечения
        val sg = segments.mapNotNull { s ->
            when (s) {
                is Segment -> s.translate(s.positionInPath(0.0).normal * h)
                is Arc -> if (s.radius + h < 0) null else Arc(
                    center = s.center,
                    radius = s.radius + h*s.normalSign,
                    outSide = s.outSide,
                    startAngle = s.startAngle,
                    sweepAngle = s.sweepAngle
                )
                //Todo:Нужно правильно вычислить перемещение
                is Curve -> s.translate(s.positionInPath(0.0).normal * h)
                else -> null
            }
        }
        return FigureRound(
            sg
        )
    }

    private val bound: vectors.BoundingRectangle by lazy<vectors.BoundingRectangle> {
        if (segments.isEmpty()) {
            vectors.BoundingRectangle(vectors.Vec2(0.0, 0.0), vectors.Vec2(0.0, 0.0))
        } else {
            var l = segments[0].start.x
            var r = segments[0].start.x
            var t = segments[0].end.y
            var b = segments[0].end.y

            segments.forEach { s ->
                val ss = s.start
                val se = s.end
                l = min(l, min(ss.x, se.x))
                r = max(r, max(ss.x, se.x))
                t = min(t, min(ss.y, se.y))
                b = max(b, max(ss.y, se.y))
            }
            vectors.BoundingRectangle(vectors.Vec2(l, t), vectors.Vec2(r, b))
        }
    }

    override fun rect(): vectors.BoundingRectangle {
        return bound
    }

    override fun segments(): PathIterator {
        return CustomPathIterator(segments)
    }
}