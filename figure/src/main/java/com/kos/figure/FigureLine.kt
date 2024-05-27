package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.Vec2
import vectors.Vec2.Companion.calcXPosition
import vectors.Vec2.Companion.calcYPosition
import kotlin.math.max
import kotlin.math.min

class FigureLine private constructor(points: List<Vec2>) : FigurePolygon(points) {

    constructor(a: Vec2, b: Vec2) : this(listOf(a, b))

    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureLine(points)
    }

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        if (points.size < 2) {
            return Empty
        }
        return cropLine(k, cropSide);
    }

    private fun cropLine(k: Double, cropSide: CropSide): IFigure {
        when (cropSide) {
            CropSide.LEFT -> {
                if (points[0].x >= k && points[1].x >= k) {
                    return this;
                }

                val r = max(points[0].x, points[1].x);
                if (r < k)
                    return Empty

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
                    return Empty

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
                    return Empty

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
                    return Empty

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
            PointWithNormal.from(Vec2.lerp(points[0], points[1], delta), points[1])
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

    override fun pathLength(edge:Int): Double {
        return if (points.size >= 2) return Vec2.distance(points[0], points[1]) else 0.0
    }

    override fun path(edge: Int): IFigure {
        return this
    }
}