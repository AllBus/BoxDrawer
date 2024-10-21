package com.kos.figure.complex.model

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
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
        val normalizedAngle = (angle % (2 * PI) + 2 * PI) % (2 * PI) // Normalize angle to [0,2pi)
        val normalizedStart = (startAngle % (2 * PI) + 2 * PI) % (2 * PI)
        val normalizedEnd = (endAngle % (2 * PI) + 2 * PI) % (2 * PI)

        return if (normalizedStart <= normalizedEnd) {
            normalizedAngle in normalizedStart..normalizedEnd
        } else {
            normalizedAngle in normalizedStart..2 * PI || normalizedAngle in 0.0..normalizedEnd
        }
    }

    override fun toFigure():FigureCircle {
        return FigureCircle(
            center = center,
            radius = radius,
            outSide = outSide,
            segmentStartAngle = startAngle,
            segmentSweepAngle = sweepAngle,
        )
    }

    override fun toPath(): IFigurePath  = toFigure()

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

    override fun take(startMM: Double, endMM: Double): Figure {
        val pe = perimeter()
        if (pe<=0 || endMM<=startMM)
            return FigureEmpty
        val st = startMM/pe
        val en = endMM/pe

        val ste = st.coerceIn(0.0, 1.0)
        val end = en.coerceIn(0.0, 1.0)
        return FigureCircle(
            center = center,
            radius = radius,
            outSide = outSide,
            segmentStartAngle = Math.toRadians(startAngle+sweepAngle*ste),
            segmentSweepAngle = Math.toRadians(sweepAngle*(end-ste))
        )
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