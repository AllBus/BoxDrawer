package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.complex.model.PathIterator
import com.kos.figure.complex.model.Segment
import com.kos.figure.complex.model.SegmentList
import com.kos.figure.complex.model.SimpleElement
import vectors.Vec2
import vectors.Vec2.Companion.calcXPosition
import vectors.Vec2.Companion.calcYPosition
import kotlin.math.max
import kotlin.math.min

class FigureLine private constructor(points: List<Vec2>) : FigurePolyline(points), FigureWithApproximation
    {

    constructor(a: Vec2, b: Vec2) : this(listOf(a, b))

    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureLine(points)
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        if (points.size < 2) {
            return FigureEmpty
        }
        return cropLine(k, cropSide);
    }

    private fun cropLine(k: Double, cropSide: CropSide): Figure {
        when (cropSide) {
            CropSide.LEFT -> {
                if (points[0].x >= k && points[1].x >= k) {
                    return this;
                }

                val r = max(points[0].x, points[1].x);
                if (r < k)
                    return FigureEmpty

                val c = calcYPosition(points[1], points[0], k);

                return FigureLine(
                    Vec2(k, c),
                    if (points[1].x > points[0].x) points[1] else points[0]
                )

            }

            CropSide.BOTTOM -> {
                if (points[0].y >= k && points[1].y >= k) {
                    return this;
                }
                val r = max(points[0].y, points[1].y);
                if (r < k)
                    return FigureEmpty

                val c = calcXPosition(points[1], points[0], k);

                return FigureLine(
                    Vec2(c, k),
                    if (points[1].y > points[0].y) points[1] else points[0]
                )

            }

            CropSide.RIGHT -> {
                if (points[0].x <= k && points[1].x <= k) {
                    return this;
                }
                val r = min(points[0].x, points[1].x);
                if (r > k)
                    return FigureEmpty

                val c = calcYPosition(points[1], points[0], k)

                return FigureLine(
                    Vec2(k, c),
                    if (points[1].x < points[0].x) points[1] else points[0]
                )

            }

            CropSide.TOP -> {
                if (points[0].y <= k && points[1].y <= k) {
                    return this;
                }
                val r = min(points[0].y, points[1].y);
                if (r > k)
                    return FigureEmpty

                val c = calcXPosition(points[1], points[0], k);

                return FigureLine(
                    Vec2(c, k),
                    if (points[1].y < points[0].y) points[1] else points[0]
                )

            }
        }
    }

    override fun draw(g: IFigureGraphics) {
        if (points.size >= 2) {
            g.drawLine(points[0], points[1])
        }
    }

    override fun print(): String {
        return "M 0 0 L " + points.map { p -> "${p.x} ${p.y}" }.joinToString(" ")
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        return if (points.size >= 2) {
            PointWithNormal.from(Vec2.lerp(points[0], points[1], delta), points[0],  points[1])
        } else
            PointWithNormal.EMPTY
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        return positionInPath(delta)
    }

    override fun name(): String {
        return "Линия"
    }

    override fun edgeCount(): Int = 1

    override fun pathLength(): Double {
        return if (points.size >= 2) return Vec2.distance(points[0], points[1]) else 0.0
    }

    override fun pathLength(edge: Int): Double {
        return if (points.size >= 2) return Vec2.distance(points[0], points[1]) else 0.0
    }

    override fun path(edge: Int): IFigurePath {
        return this
    }

    override fun duplicationAtNormal(h: Double): FigureLine {
        val s = positionInPath(0.0)
        val e = positionInPath(1.0)
        return FigureLine(points[0] + s.normal * h, points[1] + e.normal * h)
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val a = points[0]
        val b = points[1]
        val d = Vec2.distance(a, b)

        if (d <= 0.0)
            return FigureEmpty

        val sm = startMM.coerceIn(0.0, d)
        val em = endMM.coerceIn(0.0, d)

        return FigureLine(
            Vec2.lerp(a, b, sm / d),
            Vec2.lerp(a, b, em / d)
        )
    }

    override fun segments(): PathIterator {
        return SegmentList(points)
    }
}