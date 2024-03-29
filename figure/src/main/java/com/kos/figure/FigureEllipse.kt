package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

open class FigureEllipse(
    val center: Vec2,
    val radius: Double,
    val radiusMinor: Double,
    val rotation: Double,
    val segmentStart: Double = 0.0,
    val segmentEnd: Double = 0.0,
) : Figure(), IFigurePath {

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        //Todo: Правильно отрезать
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
        return BoundingRectangle.apply(center, radius, radiusMinor, rotation)
    }

    open fun create(
        center: Vec2,
        radius: Double,
        radiusMinor: Double,
        rotation: Double,
        segmentStart: Double,
        segmentEnd: Double
    ): FigureEllipse {
        return FigureEllipse(center, radius, radiusMinor, rotation, segmentStart, segmentEnd)
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return create(
            center = center + Vec2(translateX, translateY),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = rotation,
            segmentStart = segmentStart,
            segmentEnd = segmentEnd
        )
    }

    override fun rotate(angle: Double): IFigure {
        return create(
            center = center.rotate(angle),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle) % (2 * PI),
            segmentStart = segmentStart,
            segmentEnd = segmentEnd
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureEllipse(
            center = (center - rotateCenter).rotate(angle) + rotateCenter,
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle) % (2 * PI),
            segmentStart = segmentStart,
            segmentEnd = segmentEnd
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.rotate(rotation * 180 / PI, center)
        g.drawArc(center, radius, radiusMinor, segmentStart, segmentEnd)
        g.restore()
    }

    protected fun calculateSegments(s1: Double, e1: Double): IFigure {
        var ls = segmentEnd - segmentStart
        if (ls < 0) ls += 360.0
        var le = e1 - s1
        if (le < 0) le += 360.0
        var stS = normalizeAngle(segmentStart)
        var stE = stS + ls
        val atS = normalizeAngle(s1)
        val atE = atS + le

        if (stS == stE) {
            return create(
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

            val f1 = create(
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

            val f2 = create(
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

    override fun print(): String {
        return "M ${center.x} ${center.y} a $rotation e ${radius} ${radiusMinor} ${segmentStart} ${segmentEnd}"
    }

    open fun perimeter(): Double {
        if (segmentStart == segmentEnd) {
            return Math.PI * (3 * (radius + radiusMinor) - sqrt((3 * radius + radiusMinor) * (radius + 3 * radiusMinor)))
        } else {
            val startAngle = segmentStart * Math.PI / 180;
            val endAngle = segmentEnd * Math.PI / 180;
            val dt = Math.PI * 0.001
            // считаем эксцентриситет
            val a = radius
            val b = radiusMinor
            val a2 = a*a
            val b2 = b*b
            val ex = 1.0 - b * b / a * a

            var result = 0.0
            var t = startAngle
            while (t <= endAngle) {
                // сумма, интеграл
//                val ct = cos(t)
//                result += sqrt(1 - ex * ct * ct)
                val ct = cos(t)
                val ct2 = ct*ct
                val st2 = 1 - ct2
                result += sqrt(a2*st2+b2*ct2)*dt
                // sqrt(1 - ex * ct * ct) need a >= b
                t += dt
            }
            return result
            //a * result
        }
    }

    private val length by lazy { perimeter() }

    override fun positionInPath(delta: Double): PointWithNormal {
        /**
         * Уравнение эллипса
         * x^2 / a^2 + y^2 / b^2 = 1
         * Уравнение нормали
         * (y - y1) / (x - x1) = (a^2 y1) / (b^2 x1)
         *
         * Уравнение касательных
         * (y - y1) / (x - x1) = (- x1 y1 +- sqrt(b^2 x1^2 + a^2 y1^2 - a^2 b^2)) / a^2 - x1^2
         */

        // Todo Значение вычисено для круга нкжно переписать для эллипса
        val d = if (segmentStart == segmentEnd) {
            360.0
        } else
            segmentEnd

        val startAngle = segmentStart * Math.PI / 180;
        val endAngle = d * Math.PI / 180;

        val pos = if (delta <= 0) {
            Vec2(radius * cos(startAngle), radiusMinor * sin(startAngle))
        } else
            if (delta >= 1.0) {
                Vec2(radius * cos(endAngle), radiusMinor * sin(endAngle))
            } else {
                val dt = Math.PI * 0.001
                val a = radius
                val b = radiusMinor
                //val ex = 1.0 - b * b / a * a
                val a2 = a*a
                val b2 = b*b

                var result = 0.0
                var t = startAngle
                val tj = delta * length
                while (t <= endAngle && result < tj) {
                    // сумма, интеграл
                    val ct = cos(t)
                    val ct2 = ct*ct
                    val st2 = 1 - ct2
                    result += sqrt(a2*st2+b2*ct2)*dt
                    // sqrt(1 - ex * ct * ct) need a >= b
                    t += dt
                }
                Vec2(radius * cos(t), radiusMinor * sin(t))

            }

        val pr = center+  pos.rotate(rotation)

        val normal =  Vec2.normalize(Vec2.Zero, Vec2(radiusMinor*radiusMinor*pos.x, radius*radius* pos.y)).rotate(rotation)
        return PointWithNormal(pr, normal)
    }
}


