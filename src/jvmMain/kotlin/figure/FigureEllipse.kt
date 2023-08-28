package figure

import com.kos.boxdrawe.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.*

open class FigureEllipse(
    val center: Vec2,
    val radius: Double,
    val radiusMinor: Double,
    val rotation: Double,
    val segmentStart: Double = 0.0,
    val segmentEnd: Double = 0.0,
    ) : Figure(){

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

            CropSide.BOTTOM -> {
                if (center.y + radius <= k) return Empty
                if (center.y - radius >= k) return this
                val s = (k - center.y) / radius
                val s1 = asin(s) * 180 / PI
                val s2 = 180.0 - s1
                calculateSegments(s1, s2)
            }

            CropSide.TOP -> {
                if (center.y + radius <= k) return this
                if (center.y - radius >= k) return Empty
                val s = (k - center.y) / radius
                val s1 = asin(s) * 180 / PI
                val s2 = 180.0 - s1
                calculateSegments(s2, s1)
            }
        }
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.apply(center, radius, radiusMinor, rotation)
    }


    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureEllipse(
            center = center - Vec2(translateX, translateX),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = rotation,
            segmentStart = segmentStart,
            segmentEnd = segmentEnd
        )
    }

    override fun rotate(angle: Double): IFigure {
        return FigureEllipse(
            center = center.rotate(angle),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle)%(2*PI),
            segmentStart = segmentStart,
            segmentEnd = segmentEnd
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureEllipse(
            center = (center-rotateCenter).rotate(angle)+rotateCenter,
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle)%(2*PI),
            segmentStart = segmentStart,
            segmentEnd = segmentEnd
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.drawArc(center, radius, radiusMinor, segmentStart, segmentEnd)
    }

    private fun calculateSegments(s1: Double, e1: Double): IFigure {
        var ls = segmentEnd - segmentStart
        if (ls < 0) ls += 360.0
        var le = e1 - s1
        if (le < 0) le += 360.0
        var stS = normalizeAngle(segmentStart)
        var stE = stS + ls
        val atS = normalizeAngle(s1)
        val atE = atS + le

        if (stS == stE) {
            return FigureEllipse(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                segmentStart = atS,
                segmentEnd = atE,
            )
        } else {
            if (atS == atE)
                return this

            if (atS >= stE || atE <= stS)
                return Empty;

            val f1 = FigureEllipse(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                segmentStart = normalizeAngle(max(atS, stS)),
                segmentEnd = normalizeAngle(min(atE, stE)),
            )

            stS += 360;
            stE += 360;

            if (atS >= stE || atE <= stS)
                return f1;

            val f2 = FigureEllipse(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                segmentStart = normalizeAngle(max(atS, stS)),
                segmentEnd = normalizeAngle(min(atE, stE)),
            )

            return FigureList(listOf(f1, f2))
        }
    }

    private fun normalizeAngle(angle: Double): Double {
        return (angle % 360 + 360) % 360
    }
}


