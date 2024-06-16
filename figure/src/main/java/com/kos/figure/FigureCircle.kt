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
    companion object {
        private fun calcSweep(startArc: Vec2, endArc:Vec2): Double {
            val ea = endArc.angle
            val sa = startArc.angle
            if (sa > ea)
                return  Math.toDegrees(sa-ea)-360
           return  Math.toDegrees(sa-ea)
        }
    }

    constructor(center:Vec2, radius:Double, startArc: Vec2, endArc:Vec2): this(
        center, radius, -Math.toDegrees((startArc-center).angle),
       calcSweep(startArc-center, endArc-center)
    ){
        println("$segmentStart to $segmentSweep")
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return if (radius <= 0) FigureEmpty else when (cropSide) {
            CropSide.LEFT -> {
                if (center.x + radius <= k) return FigureEmpty
                if (center.x - radius >= k) return this
                val s = (k - center.x) / radius
                val s1 = acos(s) * 180 / PI
                val s2 = -s1
                calculateSegments(s2, s1)
            }

            CropSide.RIGHT -> {
                if (center.x + radius <= k) return this
                if (center.x - radius >= k) return FigureEmpty
                val s = (k - center.x) / radius
                val s1 = acos(s) * 180 / PI
                val s2 = -s1
                calculateSegments(s1, s2)
            }

            CropSide.TOP -> {
                if (center.y + radius <= k) return FigureEmpty
                if (center.y - radius >= k) return this
                val s = (center.y - k) / radius
                val s1 = asin(s) * 180 / PI
                val s2 = 180.0 - s1
                calculateSegments(s1, s2)
            }

            CropSide.BOTTOM -> {
                if (center.y + radius <= k) return this
                if (center.y - radius >= k) return FigureEmpty
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
        return if (abs(segmentSweep)<360.0) "Дуга ${radius} - ${segmentStart} ${segmentSweep}" else "Окружность ${radius}"
    }

    override fun pathLength(): Double {
        return perimeter()
    }

    override fun edgeCount(): Int = 1

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        val startAngle = segmentStart * Math.PI / 180;
        val sweepAngle = segmentSweep * Math.PI / 180;

        return listOf((0..pointCount).map { p ->
            val t = startAngle + (sweepAngle) * p.toDouble()/pointCount
            center + Vec2(radius * cos(t), radius * sin(t))
        })
    }

    override fun duplicationAtNormal(h: Double): FigureCircle {
        return FigureCircle(center, radius+h, segmentStart, segmentSweep)
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val pe = pathLength()
        if (pe<=0)
            return FigureEmpty
        val st = startMM/pe
        val en = endMM/pe

        val ste = st.coerceIn(0.0, 1.0)
        val end = en.coerceIn(0.0, 1.0)
        return FigureCircle(
            center = center,
            radius = radius,
            segmentStart = segmentStart+segmentSweep*ste,
            segmentSweep = segmentSweep*(end-ste)
        )
    }
}
