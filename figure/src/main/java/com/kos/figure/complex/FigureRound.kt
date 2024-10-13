package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import com.kos.figure.collections.FigurePath
import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.BezierCurve
import com.kos.figure.complex.model.Corner
import com.kos.figure.complex.model.PathElement
import com.kos.figure.complex.model.Segment
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.max
import kotlin.math.min

open class FigureRound(
    val segments: List<PathElement>
) : Figure(), IFigurePath {

    override val count: Int
        get() = 1

    override fun list(): List<Figure> = emptyList()

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

    override fun path(edge: Int): IFigurePath {
        if (edge >= 0 && edge < segments.size) {
            return segments[edge].toPath()
        }
        return FigureEmpty
    }

    override fun startPoint(): Vec2 {
        return if (segments.isNotEmpty())
            segments[0].start
        else
            Vec2.Zero
    }

    override fun endPoint(): Vec2 {
        return if (segments.isNotEmpty())
            segments.last().end
        else
            Vec2.Zero
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
                        result.add(s.take(ostatok, q))
                        ostatok += end - st
                        state = false
                    }
                } else {
                    if (ostatok >= q) {
                        result.add(s.toFigure())
                    } else {
                        result.add(s.take(0.0, ostatok))
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
                    is Arc -> "(${e.center} ${e.radius} ${e.startAngle} ${e.endAngle})"
                    is BezierCurve -> "(${e.p0} ${e.p1} ${e.p2} ${e.p3})"
                    else -> ""
                }
            }
        }}"
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String {
        return "Round"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

    override fun translate(translateX: Double, translateY: Double): Figure {
        return FigureRound(
            segments = segments.map {
                it.translate(Vec2(translateX, translateY))
            }
        )
    }

    override fun toFigure(): Figure {
        return this
    }

    override fun transform(matrix: Matrix): FigurePath {
        return FigurePath(
            segments.map { it.toFigure() }.map { it.transform(matrix) }
        )
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return FigurePath(segments.map { it.toFigure() }.map { it.crop(k, cropSide) })
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
                is Arc -> if (s.radius + h < 0) null else Corner(
                    center = s.center,
                    radius = s.radius + h*s.normalSign,
                    outSide = s.outSide,
                    startAngle = s.startAngle,
                    sweepAngle = s.sweepAngle
                )
                //Todo:Нужно правильно вычислить перемещение
                is BezierCurve -> s.translate(s.positionInPath(0.0).normal * h)
                else -> null
            }
        }
        return FigureRound(
            sg
        )
    }

    private val bound: BoundingRectangle by lazy<BoundingRectangle> {
        if (segments.isEmpty()) {
            BoundingRectangle(Vec2(0.0, 0.0), Vec2(0.0, 0.0))
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
            BoundingRectangle(Vec2(l, t), Vec2(r, b))
        }
    }

    override fun rect(): BoundingRectangle {
        return bound
    }
}