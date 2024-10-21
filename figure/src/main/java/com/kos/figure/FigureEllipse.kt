package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.collections.FigureList
import com.kos.figure.collections.FigurePath
import com.kos.figure.utils.EllipseUtils.normalize
import com.kos.figure.utils.EllipseUtils.normalizeAngle
//import org.apache.commons.math3.util.MathUtils.normalizeAngle
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

/**
 *  @param segmentStartAngle radians
 *  @param segmentSweepAngle radians
 */
open class FigureEllipse(
    val center: Vec2,
    val radius: Double,
    val radiusMinor: Double,
    /** idRadians*/
    val rotation: Double,
    val outSide: Boolean,
    val segmentStartAngle: Double = 0.0,
    val segmentSweepAngle: Double = PI*2,
) : Figure(), IFigurePath, FigureWithApproximation, IRotable {

    override fun crop(k: Double, cropSide: CropSide): Figure {
        //Todo: Правильно отрезать
        return if (radius <= 0) FigureEmpty else when (cropSide) {
            CropSide.LEFT -> {
                if (center.x + radius <= k) return FigureEmpty
                if (center.x - radius >= k) return this
                val s = (k - center.x) / radius
                val s1 = acos(s) /* 0 .. PI */

                calculateSegments(-acos(s), 2 * s1)
            }

            CropSide.RIGHT -> {
                if (center.x + radius <= k) return this
                if (center.x - radius >= k) return FigureEmpty
                val s = (k - center.x) / radius
                val s1 = acos(s)
                calculateSegments(s1, 2 * (PI - s1))
            }

            CropSide.TOP -> {
                if (center.y + radiusMinor <= k) return FigureEmpty
                if (center.y - radiusMinor >= k) return this
                val s = (center.y - k) / radiusMinor
                val s1 = -asin(s) /* -PI/2 .. PI/2 */
                calculateSegments(PI - s1, PI + 2 * s1)
            }

            CropSide.BOTTOM -> {
                if (center.y + radiusMinor <= k) return this
                if (center.y - radiusMinor >= k) return FigureEmpty
                val s = (center.y - k) / radiusMinor
                val s1 = -asin(s) /* -PI/2 .. PI/2 */
                calculateSegments(s1, (PI - 2 * s1))
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
        outSide: Boolean,
        segmentStart: Double,
        segmentSweep: Double
    ): FigureEllipse {
        return FigureEllipse(
            center = center,
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = rotation,
            outSide = outSide,
            segmentStartAngle = segmentStart,
            segmentSweepAngle = if (segmentSweep == 0.0) PI * 2 else segmentSweep
        )
    }

    override fun translate(translateX: Double, translateY: Double): FigureEllipse {
        return create(
            center = center + Vec2(translateX, translateY),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = rotation,
            outSide = outSide,
            segmentStart = segmentStartAngle,
            segmentSweep = segmentSweepAngle
        )
    }

    override fun rotate(angle: Double): FigureEllipse {
        return create(
            center = center.rotate(angle),
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle) % (2 * PI),
            outSide = outSide,
            segmentStart = segmentStartAngle,
            segmentSweep = segmentSweepAngle
        )
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): FigureEllipse {
        return FigureEllipse(
            center = (center + rotateCenter).rotate(angle) - rotateCenter,
            radius = radius,
            radiusMinor = radiusMinor,
            rotation = (rotation + angle) % (2 * PI),
            outSide = outSide,
            segmentStartAngle = segmentStartAngle,
            segmentSweepAngle = segmentSweepAngle
        )
    }


    override fun transform(matrix: Matrix): FigureEllipse {
        // Todo: вычисление поворота и радиуса неправильное

        val a = matrix[0, 0].toDouble()
        val b = matrix[0, 1].toDouble()
        val c = matrix[1, 0].toDouble()
        val d = matrix[1, 1].toDouble()
        var delta = a * d - b * c;
        val newRot = if (a != 0.0 || b != 0.0) {
            val r = Math.sqrt(a * a + b * b)
            if (b > 0.0) Math.acos(a / r) else -Math.acos(a / r)
        } else if (c != 0.0 || d != 0.0) {
            val s = Math.sqrt(c * c + d * d);
            Math.PI / 2.0 - (if (d > 0.0) Math.acos(-c / s) else -Math.acos(c / s))
        } else {
            0.0
        }


        return FigureEllipse(
            center = matrix.map(center),
            radius = radius * matrix[0, 0],
            radiusMinor = radiusMinor * matrix[1, 1],
            rotation = rotation + newRot * 180.0 / Math.PI,
            outSide = outSide,
            segmentStartAngle = segmentStartAngle,
            segmentSweepAngle = segmentSweepAngle
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.rotate(rotation * 180 / PI, center)
        g.drawArc(center, radius, radiusMinor, segmentStartAngle, segmentSweepAngle)
        g.restore()
    }

    protected fun calculateSegments(start: Double, sweep: Double): Figure {

        if (isFill()) {
            return create(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                outSide = outSide,
                segmentStart = start,
                segmentSweep = sweep,
            )
        }

        val sts = normalizeAngle(segmentStartAngle) /* 0 .. 2*PI */
        val (aa, bb) = normalize(sts, segmentSweepAngle) /* 0 .. 2*PI to  0 .. 4*PII */

        val (cc, dd) = normalize(start, sweep)


        val a = 0.0
        val b = bb - aa // 0 .. 2*PI
        val c = cc - aa // - 2*PI  .. 2*PI
        val d = dd - aa

        val c2 = c + 2 * PI
        val d2 = d + 2 * PI

        val c3 = c - 2 * PI
        val d3 = d - 2 * PI

        val mis = max(a, c)
        val mas = min(b, d)

        val fa = if (mis < mas) {
            create(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                outSide = outSide,
                segmentStart = mis + aa,
                segmentSweep = mas - mis,
            )
        } else FigureEmpty

        val mis2 = max(a, c2)
        val mas2 = min(b, d2)

        val fb = if (mis2 < mas2) {
            create(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                outSide = outSide,
                segmentStart = mis2 + aa - 2 * PI,
                segmentSweep = mas2 - mis2,
            )
        } else FigureEmpty

        val mis3 = max(a, c3)
        val mas3 = min(b, d3)

        val fc = if (mis3 < mas3) {
            create(
                center = center,
                radius = radius,
                radiusMinor = radiusMinor,
                rotation = rotation,
                outSide = outSide,
                segmentStart = mis3 + aa + 2 * PI,
                segmentSweep = mas3 - mis3,
            )
        } else FigureEmpty


        val l = listOf(fa, fb, fc).filter { it != FigureEmpty }

        return when (l.size) {
            1 -> l[0]
            2 -> FigurePath(l)
            else -> FigureEmpty
        }
    }

    override fun print(): String {
        return "M ${center.x} ${center.y} a ${Math.toDegrees(rotation)} e ${radius} ${radiusMinor} ${Math.toDegrees(segmentStartAngle)} ${Math.toDegrees(segmentSweepAngle)}"
    }

    fun isFill(): Boolean {
        return (segmentSweepAngle == 0.0 || abs(segmentSweepAngle) >= PI*2)
    }

    fun endAngle(): Double {
        return if (isFill()) {
            segmentStartAngle + PI*2
        } else
            segmentStartAngle + segmentSweepAngle
    }

    open fun perimeter(): Double {
        if (isFill()) {
            return Math.PI * (3 * (radius + radiusMinor) - sqrt((3 * radius + radiusMinor) * (radius + 3 * radiusMinor)))
        } else {
            val startAngle = segmentStartAngle;
            val endAngle = endAngle()
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


        val startAngleA = segmentStartAngle
        val endAngleB = endAngle()

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
            Vec2(radiusMinor * radiusMinor * pos.x, radius * radius * pos.y)*normalSign
        ).rotate(rotation)
        return PointWithNormal(pr, normal)
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        return positionInPath(delta)
    }

    val normalSign :  Double get() = if (outSide) 1.0 else -1.0

    companion object {
        val digitFormatter =  DecimalFormat().apply{ setMaximumFractionDigits(3) }
    }

    override fun name(): String {
        if (isFill()){
            return "Эллипс ${digitFormatter.format(radius)} x ${digitFormatter.format(radiusMinor)}"
        }else {
            return "Эллипс ${digitFormatter.format(radius)} x ${digitFormatter.format(radiusMinor)} : ${
                digitFormatter.format(
                    Math.toDegrees(segmentStartAngle)
                )
            } x ${digitFormatter.format(Math.toDegrees(segmentSweepAngle))}"
        }
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        val startAngle = segmentStartAngle
        val sweepAngle = segmentSweepAngle

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
        val t = segmentStartAngle
        return center + Vec2(radius * cos(t), radiusMinor * sin(t)).rotate(rotation)
    }

    override fun endPoint(): Vec2 {
        val t = (segmentStartAngle+segmentSweepAngle)
        return center + Vec2(radius * cos(t), radiusMinor * sin(t)).rotate(rotation)
    }

    override fun duplicationAtNormal(h: Double): Figure {
        return FigureEllipse(
            center = center,
            radius = radius+h*normalSign,
            radiusMinor = radiusMinor+h*normalSign,
            rotation = rotation,
            outSide = outSide,
            segmentStartAngle = segmentStartAngle,
            segmentSweepAngle = segmentSweepAngle,
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
            outSide = outSide,
            segmentStartAngle = segmentStartAngle+segmentSweepAngle*st,
            segmentSweepAngle = segmentSweepAngle*(end-st),
        )
    }

    override fun toFigure(): Figure = this

}


