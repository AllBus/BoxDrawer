package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin

class FigureCircle(
    center: Vec2,
    radius: Double,
    segmentStart: Double = 0.0,
    segmentEnd: Double = 0.0,
    ): FigureEllipse(
    center = center,
    radius = radius,
    radiusMinor = radius,
    rotation = 0.0,
    segmentStart = segmentStart,
    segmentEnd = segmentEnd
){
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

    override fun create(center: Vec2,
                        radius: Double,
                        radiusMinor: Double,
                        rotation: Double,
                        segmentStart: Double,
                        segmentEnd: Double): FigureEllipse {
        return FigureCircle(center, radius, segmentStart, segmentEnd)
    }

    override fun draw(g: IFigureGraphics) {
        if (segmentStart == segmentEnd) {
            g.drawCircle(center, radius)
        }else{
            g.drawArc(center, radius, radiusMinor, segmentStart, segmentEnd)
        }
    }

    private fun normalizeAngle(angle: Double): Double {
        return (angle % 360 + 360) % 360
    }

    override fun print(): String {
        return "M ${center.x} ${center.y} c ${radius} ${segmentStart} ${segmentEnd}"
    }
}
