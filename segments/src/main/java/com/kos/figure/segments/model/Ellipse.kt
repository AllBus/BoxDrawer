package com.kos.figure.segments.model

import com.kos.drawer.IFigureGraphics
import vectors.PointWithNormal
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface Ellipse : PathElement {
    val center: Vec2
    val radiusX: Double
    val radiusY: Double
    val rotation: Double
    val startAngle: Double
    val endAngle: Double
    val outSide: Boolean

    val sweepAngle: Double get() = endAngle - startAngle

    fun containsAngle(angle: Double): Boolean {
        if (abs(sweepAngle) >= 2 * PI) return true

        val minus = endAngle < startAngle

        val normalizedAngle = (angle % (2 * PI) + 2 * PI) % (2 * PI) // Normalize angle to [0,2pi)
        var normalizedStart = (startAngle % (2 * PI) + 2 * PI) % (2 * PI)
        var normalizedEnd = (endAngle % (2 * PI) + 2 * PI) % (2 * PI)

        if (minus) {
            val tmp = normalizedStart
            normalizedStart = normalizedEnd
            normalizedEnd = tmp
        }

        return if (normalizedStart <= normalizedEnd) {
            normalizedAngle in normalizedStart..normalizedEnd
        } else {
            normalizedAngle in normalizedStart..2 * PI || normalizedAngle in 0.0..normalizedEnd
        }
    }

    fun isFill(): Boolean {
        return abs(sweepAngle) >= PI * 2
    }

    override fun perimeter(): Double {
        if (isFill()) {
            return Math.PI * (3 * (radiusX + radiusY) - sqrt((3 * radiusX + radiusY) * (radiusX + 3 * radiusY)))
        } else {
            val dt = Math.PI * 0.001
            // считаем эксцентриситет
            val a = radiusX
            val b = radiusY
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

    val normalSign get() = if (outSide) 1.0 else -1.0

    val length: Double

    fun position(t: Double): Vec2 {
        val rot = (startAngle + t * sweepAngle)
        return center + Vec2(radiusX * cos(rot), radiusY * sin(rot)).rotate(rotation)
    }

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

        val startAngleA = startAngle
        val endAngleB = endAngle

        val (startAngle, endAngle) = if (startAngleA < endAngleB) {
            startAngleA to endAngleB
        } else {
            endAngleB to startAngleA
        }


        val pos = if (delta <= 0) {
            Vec2(radiusX * cos(startAngle), radiusY * sin(startAngle))
        } else
            if (delta >= 1.0) {
                Vec2(radiusX * cos(endAngle), radiusY * sin(endAngle))
            } else {
                val dt = Math.PI * 0.001
                val a = radiusX
                val b = radiusY
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
                Vec2(radiusX * cos(t), radiusY * sin(t))

            }

        val pr = center + pos.rotate(rotation)

        val normal = Vec2.normalize(
            Vec2.Zero,
            Vec2(radiusY * radiusY * pos.x, radiusX * radiusX * pos.y) * normalSign
        ).rotate(rotation)
        return PointWithNormal(pr, normal)
    }

    override fun pointAt(t: Double): Vec2 {
        val rot = (startAngle + t * sweepAngle)
        val pos = center + Vec2(radiusX * cos(rot), radiusY * sin(rot)).rotate(rotation)
        return pos
    }

    override fun translate(xy: Vec2): Ellipse {
        return EllipseImpl(
            center = center + xy,
            radiusX = radiusX,
            radiusY = radiusY,
            rotation = rotation,
            startAngle = startAngle,
            endAngle = endAngle,
            outSide = outSide,
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.rotate(rotation * 180 / PI, center)
        g.drawArc(center, radiusX, radiusY, startAngle, sweepAngle)
        g.restore()
    }

    override val start: Vec2
        get() = center + Vec2(radiusX * cos(startAngle), radiusY * sin(startAngle)).rotate(rotation)
    override val end: Vec2
        get() = center + Vec2(radiusX * cos(endAngle), radiusY * sin(endAngle)).rotate(rotation)

    override fun distance(point: Vec2): Double {
        if (abs(radiusX - radiusY) < 0.0001) {
            val distToCenter = Vec2.distance(point, center)
            val angle = (point - center).angle
            return if (containsAngle(angle)) {
                abs(distToCenter - radiusX)
            } else {
                minOf(Vec2.distance(point, start), Vec2.distance(point, end))
            }
        }
        // 1. Перевод точки в локальные координаты эллипса
        val relativePoint = (point - center).rotate(-rotation)
        val px = abs(relativePoint.x)
        val py = abs(relativePoint.y)

        // 2. Поиск ближайшей точки на полном эллипсе (алгоритм приближения)
        var tx = 0.707
        var ty = 0.707

        val a = radiusX
        val b = radiusY

        for (i in 0..3) {
            val x = a * tx
            val y = b * ty
            val ex = (a * a - b * b) * tx * tx * tx / a
            val ey = (b * b - a * a) * ty * ty * ty / b
            val rx = x - ex
            val ry = y - ey
            val qx = px - ex
            val qy = py - ey
            val r = sqrt(rx * rx + ry * ry)
            val q = sqrt(qx * qx + qy * qy)
            tx = (qx * r / q + ex) / a
            ty = (qy * r / q + ey) / b
            val t = sqrt(tx * tx + ty * ty)
            tx /= t
            ty /= t
        }

        val nearestLocal = Vec2(
            if (relativePoint.x < 0) -a * tx else a * tx,
            if (relativePoint.y < 0) -b * ty else b * ty
        )

        // 3. Проверка диапазона углов для дуги
        if (isFill()) {
            return Vec2.distance(relativePoint, nearestLocal)
        } else {
            val angle = nearestLocal.angle
            return if (containsAngle(angle)) {
                Vec2.distance(relativePoint, nearestLocal)
            } else {
                minOf(Vec2.distance(point, start), Vec2.distance(point, end))
            }
        }
    }

    companion object {
        operator fun invoke(
            center: Vec2,
            radiusX: Double,
            radiusY: Double,
            rotation: Double,
            startAngle: Double,
            endAngle: Double,
            outSide: Boolean,
        ): Ellipse {
            return EllipseImpl(
                center = center,
                radiusX = radiusX,
                radiusY = radiusY,
                rotation = rotation,
                startAngle = startAngle,
                endAngle = endAngle,
                outSide = outSide,
            )
        }
    }
}

data class EllipseImpl(
    override val center: Vec2,
    override val radiusX: Double,
    override val radiusY: Double,
    override val rotation: Double,
    override val startAngle: Double,
    override val endAngle: Double,
    override val outSide: Boolean,
) : Ellipse {
    override val length by lazy { perimeter() }
}