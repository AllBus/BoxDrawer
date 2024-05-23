package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

/**
 *  @param segmentStart degrees
 *  @param segmentEnd degrees
 */
class FigureCircle(
    center: Vec2,
    radius: Double,
    segmentStart: Double = 0.0,
    segmentSweep: Double = 360.0,
) : FigureEllipse(
    center = center,
    radius = radius,
    radiusMinor = radius,
    rotation = 0.0,
    segmentStart = segmentStart,
    segmentSweep = segmentSweep
) {
    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return if (radius <= 0) Empty else when (cropSide) {
            CropSide.LEFT -> {
                if (center.x + radius <= k) return Empty
                if (center.x - radius >= k) return this
                val s = (k - center.x) / radius
                val s1 = acos(s) * 180 / PI
                val s2 = -s1
                calculateSegments(s2, s1)
            }

            CropSide.RIGHT -> {
                if (center.x + radius <= k) return this
                if (center.x - radius >= k) return Empty
                val s = (k - center.x) / radius
                val s1 = acos(s) * 180 / PI
                val s2 = -s1
                calculateSegments(s1, s2)
            }

            CropSide.TOP -> {
                if (center.y + radius <= k) return Empty
                if (center.y - radius >= k) return this
                val s = (center.y - k) / radius
                val s1 = asin(s) * 180 / PI
                val s2 = 180.0 - s1
                calculateSegments(s1, s2)
            }

            CropSide.BOTTOM -> {
                if (center.y + radius <= k) return this
                if (center.y - radius >= k) return Empty
                val s = (center.y - k) / radius
                val s1 = asin(s) * 180 / PI
                val s2 = 180.0 - s1
                calculateSegments(s2, s1)
            }
        }
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(center, radius)
    }

    override fun create(
        center: Vec2,
        radius: Double,
        radiusMinor: Double,
        rotation: Double,
        segmentStart: Double,
        segmentSweep: Double
    ): FigureEllipse {
        return FigureCircle(center, radius, segmentStart, segmentSweep)
    }

    override fun draw(g: IFigureGraphics) {
        if (isFill()) {
            g.drawCircle(center, radius)
        } else {
            g.drawArc(center, radius, radiusMinor, segmentStart, segmentSweep)
        }
    }

    private fun normalizeAngle(angle: Double): Double {
        return (angle % 360 + 360) % 360
    }

    override fun print(): String {
        return "M ${center.x} ${center.y} c ${radius} ${segmentStart} ${segmentSweep}"
    }

    override fun perimeter(): Double {
        if (isFill()) return 2 * Math.PI * radius
        return abs((segmentSweep) * Math.PI / 180.0 * radius)
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        val rot = (segmentStart + delta * segmentSweep) * Math.PI / 180
        val pos = center + Vec2(radius, 0.0).rotate(rot)
        val normal = Vec2(1.0, 0.0).rotate(rot)
        return PointWithNormal(pos, normal)
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        return positionInPath(delta)
    }

    override fun name(): String {
        return "Круг"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        val startAngle = segmentStart * Math.PI / 180;
        val sweepAngle = segmentSweep * Math.PI / 180;

        return listOf((0..pointCount).map { p ->
            val t = startAngle + (sweepAngle) * p.toDouble()/pointCount
            center + Vec2(radius * cos(t), radius * sin(t))
        })
    }
}
