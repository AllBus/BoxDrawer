package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import java.text.DecimalFormat
import kotlin.math.PI
import kotlin.math.abs
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
    val segmentSweep: Double = 360.0,
) : Figure(), IFigurePath, Approximation {

    override fun crop(k: Double, cropSide: CropSide): Figure {
        //Todo: Правильно отрезать
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
        return BoundingRectangle.apply(center, radius, radiusMinor, rotation)
    }

    open fun create(
        center: Vec2,
        radius: Double,
        radiusMinor: Double,
        rotation: Double,
        segmentStart: Double,
        segmentSweep: Double
    ): FigureEllipse {
        return FigureEllipse(center, radius, radiusMinor, rotation, segmentStart,
            if (segmentSweep == 0.0) 360.0 else segmentSweep
        )
    }

    override fun translate(translateX: Double, translateY: Double): FigureEllipse {
        return create(
            center = center + Vec2(translateX, translateY),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = rotation,
            segmentStart = segmentStart,
            segmentSweep = segmentSweep
        )
    }

    override fun rotate(angle: Double): FigureEllipse {
        return create(
            center = center.rotate(angle),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle) % (2 * PI),
            segmentStart = segmentStart,
            segmentSweep = segmentSweep
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): FigureEllipse {
        return FigureEllipse(
            center = (center - rotateCenter).rotate(angle) + rotateCenter,
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle) % (2 * PI),
            segmentStart = segmentStart,
            segmentSweep = segmentSweep
        )
    }


    override fun transform(matrix: Matrix): FigureEllipse {



        // Todo: вычисление поворота и радиуса неправильное
        val r = matrix.map(Vec2(radius, radiusMinor))
        return FigureEllipse(
            center = matrix.map(center),
            radius = r.x,
            radiusMinor = r.y,
            rotation = rotation,
            segmentStart = segmentStart,
            segmentSweep = segmentSweep
        )
    }


    override fun draw(g: IFigureGraphics) {
        g.save()
        g.rotate(rotation * 180 / PI, center)
        g.drawArc(center, radius, radiusMinor, segmentStart, segmentSweep)
        g.restore()
    }

    protected fun calculateSegments(s1: Double, e1: Double): Figure {
        //Todo:
        var ls = segmentSweep
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
                segmentSweep = atE,
            )
        } else {
            if (atS == atE)
                return this

            if (atS >= stE || atE <= stS)
                return FigureEmpty;

            val f1 = create(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                segmentStart = normalizeAngle(max(atS, stS)),
                segmentSweep = normalizeAngle(min(atE, stE)),
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
                segmentSweep = normalizeAngle(min(atE, stE)),
            )

            return FigurePath(listOf(f1, f2))
        }
    }

    private fun normalizeAngle(angle: Double): Double {
        return (angle % 360 + 360) % 360
    }

    override fun print(): String {
        return "M ${center.x} ${center.y} a $rotation e ${radius} ${radiusMinor} ${segmentStart} ${segmentSweep}"
    }

    fun isFill(): Boolean {
        return (segmentSweep == 0.0 || abs(segmentSweep) >= 360.0)
    }

    fun endAngle(): Double {
        return if (isFill()) {
            segmentStart + 360.0
        } else
            segmentStart + segmentSweep
    }

    open fun perimeter(): Double {
        if (isFill()) {
            return Math.PI * (3 * (radius + radiusMinor) - sqrt((3 * radius + radiusMinor) * (radius + 3 * radiusMinor)))
        } else {
            val startAngle = segmentStart * Math.PI / 180;
            val endAngle = endAngle() * Math.PI / 180
            val dt = Math.PI * 0.001
            // считаем эксцентриситет
            val a = radius
            val b = radiusMinor
            val a2 = a * a
            val b2 = b * b
            val ex = 1.0 - b * b / a * a

            var result = 0.0
            var t = startAngle
            while (t <= endAngle) {
                // сумма, интеграл
//                val ct = cos(t)
//                result += sqrt(1 - ex * ct * ct)
                val ct = cos(t)
                val ct2 = ct * ct
                val st2 = 1 - ct2
                result += sqrt(a2 * st2 + b2 * ct2) * dt
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


        val startAngleA = segmentStart * Math.PI / 180;
        val endAngleB = endAngle() * Math.PI / 180

        val (startAngle, endAngle) = if (startAngleA < endAngleB) {
            startAngleA to endAngleB
        } else {
            endAngleB to startAngleA
        }


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
                val a2 = a * a
                val b2 = b * b

                var result = 0.0
                var t = startAngle
                val tj = delta * length
                while (t <= endAngle && result < tj) {
                    // сумма, интеграл
                    val ct = cos(t)
                    val ct2 = ct * ct
                    val st2 = 1 - ct2
                    result += sqrt(a2 * st2 + b2 * ct2) * dt
                    // sqrt(1 - ex * ct * ct) need a >= b
                    t += dt
                }
                Vec2(radius * cos(t), radiusMinor * sin(t))

            }

        val pr = center + pos.rotate(rotation)

        val normal = Vec2.normalize(
            Vec2.Zero,
            Vec2(radiusMinor * radiusMinor * pos.x, radius * radius * pos.y)
        ).rotate(rotation)
        return PointWithNormal(pr, normal)
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        return positionInPath(delta)
    }

    companion object {
        val digitFormatter =  DecimalFormat().apply{ setMaximumFractionDigits(3) }
    }

    override fun name(): String {
        return "Эллипс ${digitFormatter.format(radius)} : ${digitFormatter.format(radiusMinor)} - ${digitFormatter.format(segmentStart)} : ${digitFormatter.format(segmentSweep)}"
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        val startAngle = segmentStart * Math.PI / 180
        val sweepAngle = segmentSweep * Math.PI / 180

        return listOf((0..pointCount).map { p ->
            val t = startAngle + sweepAngle * p.toDouble() / pointCount
            center + Vec2(radius * cos(t), radiusMinor * sin(t)).rotate(rotation)
        })
    }

    override fun pathLength(): Double {
        return length
    }

    override fun pathLength(edge: Int): Double {
        return length
    }

    override fun edgeCount(): Int = 1

    override fun path(edge: Int): IFigurePath {
        return this
    }

    override fun startPoint(): Vec2 {
        val t = segmentStart * Math.PI / 180
        return center + Vec2(radius * cos(t), radiusMinor * sin(t)).rotate(rotation)
    }

    override fun endPoint(): Vec2 {
        val t = (segmentStart+segmentSweep) * Math.PI / 180
        return center + Vec2(radius * cos(t), radiusMinor * sin(t)).rotate(rotation)
    }

    override fun duplicationAtNormal(h: Double): Figure {
        return FigureEllipse(
            center = center,
            radius = radius+h,
            radiusMinor = radiusMinor+h,
            rotation = rotation,
            segmentStart = segmentStart,
            segmentSweep = segmentSweep
        )
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val st = startMM/pathLength()
        val end = endMM/pathLength()
        //Todo: Вычислить правильный сегмент
        return FigureEllipse(
            center = center,
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = rotation,
            segmentStart = segmentStart+segmentSweep*st,
            segmentSweep = segmentSweep*(end-st)
        )
    }

    override fun toFigure(): Figure = this

}


