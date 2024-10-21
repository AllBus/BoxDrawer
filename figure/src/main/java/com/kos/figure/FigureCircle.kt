package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.FigureEllipse.Companion.digitFormatter
import com.kos.figure.collections.FigurePath
import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.CustomPathIterator
import com.kos.figure.complex.model.PathIterator
import com.kos.figure.complex.model.SimpleElement
import com.kos.figure.utils.EllipseUtils
import com.kos.figure.utils.EllipseUtils.normalize
import com.kos.figure.utils.EllipseUtils.normalizeAngle
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 *  @param segmentStartAngle radians
 *  @param segmentSweepAngle radians
 */
class FigureCircle(
    val center: Vec2,
    val radius: Double,
    val outSide: Boolean,
    val segmentStartAngle: Double = 0.0,
    val segmentSweepAngle: Double = PI*2
) : Figure(), IFigurePath, FigureWithApproximation, IRotable, SimpleElement{

    companion object {
        private fun calcSweep(startArc: Vec2, endArc:Vec2): Double {
            val ea = startArc.angle
            val sa = endArc.angle
            if (sa > ea)
                return  sa-ea-PI*2
           return sa-ea
        }
    }

    constructor(center:Vec2, radius:Double,outSide: Boolean, startArc: Vec2, endArc:Vec2 ): this(
        center, radius, outSide,  (startArc-center).angle,
       calcSweep(startArc-center, endArc-center)
    ){

    }

    override fun translate(translateX: Double, translateY: Double): Figure {
        return create(
            center = center + Vec2(translateX, translateY),
            radius = radius,
            outSide = outSide,
            segmentStart = segmentStartAngle,
            segmentSweep = segmentSweepAngle
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
            radiusMinor = radius * matrix[1, 1],
            rotation =  newRot * 180.0 / Math.PI,
            outSide = outSide,
            segmentStartAngle = segmentStartAngle,
            segmentSweepAngle = segmentSweepAngle
        )
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return if (radius <= 0) FigureEmpty else when (cropSide) {
            CropSide.LEFT -> {
                if (center.x + radius <= k) return FigureEmpty
                if (center.x - radius >= k) return this
                val s = (k - center.x) / radius
                val s1 = acos(s) /* 0 .. PI */

                calculateSegments(-acos(s), 2*s1)
            }

            CropSide.RIGHT -> {
                if (center.x + radius <= k) return this
                if (center.x - radius >= k) return FigureEmpty
                val s = (k - center.x) / radius
                val s1 = acos(s)
                calculateSegments(s1, 2*(PI - s1))
            }

            CropSide.TOP -> {
                if (center.y + radius <= k) return this
                if (center.y - radius >= k) return FigureEmpty

                val s = (center.y - k) / radius
                val s1 = -asin(s) /* -PI/2 .. PI/2 */
                calculateSegments(PI- s1, PI  + 2*s1)
            }

            CropSide.BOTTOM -> {
                if (center.y + radius <= k) return FigureEmpty
                if (center.y - radius >= k) return this
                val s = (center.y - k) / radius
                val s1 = -asin(s) /* -PI/2 .. PI/2 */
                calculateSegments(s1, (PI - 2*s1))
            }
        }
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(center, radius)
    }

    override fun toFigure(): Figure {
        return this
    }

    override fun rotate(angle: Double): FigureCircle {
        return FigureCircle(center.rotate(angle), radius, outSide, EllipseUtils.normalizeAngle(segmentStartAngle+angle), segmentSweepAngle)
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): FigureCircle {
        return FigureCircle((center + rotateCenter).rotate(angle) - rotateCenter, radius,outSide, EllipseUtils.normalizeAngle(segmentStartAngle+angle), segmentSweepAngle)
    }

    fun create(
        center: Vec2,
        radius: Double,
        outSide: Boolean,
        segmentStart: Double,
        segmentSweep: Double
    ): FigureCircle {
        return FigureCircle(
            center = center,
            radius = radius,
            outSide = outSide,
            segmentStartAngle = segmentStart,
            segmentSweepAngle = segmentSweep
        )
    }

    override fun draw(g: IFigureGraphics) {
        if (isFill()) {
            g.drawCircle(center, radius)
        } else {
            g.drawArc(center, radius, radius, segmentStartAngle, segmentSweepAngle)
        }
    }

    override fun print(): String {
        return "M ${center.x} ${center.y} c ${radius} ${Math.toDegrees(segmentStartAngle)} ${Math.toDegrees(segmentStartAngle+segmentSweepAngle)}"
    }

    fun isFill(): Boolean {
        return (segmentSweepAngle == 0.0 || abs(segmentSweepAngle) >= PI*2)
    }

    val normalSign :  Double get() = if (outSide) 1.0 else -1.0

    fun perimeter(): Double {
        if (isFill()) return 2 * Math.PI * radius
        return abs(segmentSweepAngle * radius)
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        val rot = (segmentStartAngle + delta * segmentSweepAngle)
        val pos = center + Vec2(radius, 0.0).rotate(rot)
        val normal = Vec2(1.0*normalSign, 0.0).rotate(rot)
        return PointWithNormal(pos, normal)
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        return positionInPath(delta)
    }

    override fun name(): String {
        return if (abs(segmentSweepAngle)<PI*2)
            "Дуга ${digitFormatter.format(radius)} : ${digitFormatter.format(Math.toDegrees(segmentStartAngle))} x ${digitFormatter.format(Math.toDegrees(segmentSweepAngle))}"
        else "Окружность ${digitFormatter.format(radius)}"
    }

    override fun pathLength(): Double {
        return perimeter()
    }

    override fun pathLength(edge: Int): Double {
        return perimeter()
    }

    override fun edgeCount(): Int = 1
    override fun path(edge: Int): IFigurePath {
        return this
    }

    override fun startPoint(): Vec2 {
        val t = segmentStartAngle
        return center + Vec2(radius * cos(t), radius * sin(t))
    }

    override fun endPoint(): Vec2 {
        val t = (segmentStartAngle+segmentSweepAngle)
        return center + Vec2(radius * cos(t), radius * sin(t))
    }

    override fun approximate(pointCount: Int): List<List<Vec2>> {
        val startAngle = segmentStartAngle
        val sweepAngle = segmentSweepAngle

        return listOf((0..pointCount).map { p ->
            val t = startAngle + (sweepAngle) * p.toDouble()/pointCount
            center + Vec2(radius * cos(t), radius * sin(t))
        })
    }


    override fun duplicationAtNormal(h: Double): FigureCircle {
        return FigureCircle(center, radius+h*normalSign, outSide, segmentStartAngle, segmentSweepAngle)
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
            outSide = outSide,
            segmentStartAngle = segmentStartAngle+segmentSweepAngle*ste,
            segmentSweepAngle = segmentSweepAngle*(end-ste)
        )
    }

    override fun segments(): PathIterator {
        return CustomPathIterator(listOf(Arc(
            center = center,
            radius = radius,
            outSide = outSide,
            startAngle = segmentStartAngle,
            sweepAngle = segmentSweepAngle
        )))
    }

    protected fun calculateSegments(start: Double, sweep: Double): Figure {

        if (isFill()) {
            return create(
                center = center,
                radius = radius,
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
}
