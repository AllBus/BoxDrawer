package com.kos.figure.segments.model

import com.kos.drawer.IFigureGraphics
import vectors.PointWithNormal
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs

interface Arc: PathElement {
    val center: Vec2
    val radius: Double
    val startAngle: Double
    val endAngle: Double
    val outSide: Boolean

    val sweepAngle : Double  get() = endAngle-startAngle

    fun containsAngle(angle: Double): Boolean {
        if (abs(sweepAngle) >= 2* PI) return true

        val minus = endAngle < startAngle

        val normalizedAngle = (angle % (2 * PI) + 2 * PI) % (2 * PI) // Normalize angle to [0,2pi)
        var normalizedStart = (startAngle % (2 * PI) + 2 * PI) % (2 * PI)
        var normalizedEnd = (endAngle % (2 * PI) + 2 * PI) % (2 * PI)

        if (minus) {
            val tmp = normalizedStart
            normalizedStart = normalizedEnd
            normalizedEnd = tmp
        }

        //println("contain ${angle} $normalizedAngle : $normalizedStart $normalizedEnd")
        return if (normalizedStart <= normalizedEnd) {
            normalizedAngle in normalizedStart..normalizedEnd
        } else {
            normalizedAngle in normalizedStart..2 * PI || normalizedAngle in 0.0..normalizedEnd
        }
    }



    override fun perimeter(): Double {
        return abs(sweepAngle * radius)
    }

    val normalSign get() = if (outSide) 1.0 else -1.0

    override fun positionInPath(delta: Double): PointWithNormal {
        val rot = (startAngle + delta * sweepAngle)
        val pos = center + Vec2(radius, 0.0).rotate(rot)
        val normal = Vec2(1.0*normalSign, 0.0).rotate(rot)
        return PointWithNormal(pos, normal)
    }

    override fun pointAt(t: Double): Vec2 {
        val rot = (startAngle + t * sweepAngle)
        val pos = center + Vec2(radius, 0.0).rotate(rot)
        return pos
    }

    override fun translate(xy: Vec2): Arc {
        return ArcImpl(
            center = center+xy,
            radius = radius,
            outSide = outSide,
            startAngle = startAngle,
            endAngle = endAngle
        )
    }

    override fun draw(g: IFigureGraphics) {
        g.drawArc(center, radius, radius, startAngle, sweepAngle)
    }

    override val start: Vec2
        get() = center + Vec2(radius, 0.0).rotate(startAngle)
    override val end: Vec2
        get() = center + Vec2(radius, 0.0).rotate(startAngle+sweepAngle)

    companion object {
        operator fun invoke(
            center: Vec2,
            radius:Double,
            outSide: Boolean,
            startAngle:Double,
            sweepAngle:Double,
        ): Arc {
            return ArcImpl(
                center = center,
                radius = radius,
                startAngle = startAngle,
                endAngle = startAngle+sweepAngle,
                outSide = outSide,
            )
        }
    }
}

data class ArcImpl(
    override val center: Vec2,
    override val radius: Double,
    override val startAngle: Double,
    override val endAngle: Double,
    override val outSide: Boolean
): Arc