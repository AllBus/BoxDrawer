package com.kos.figure.complex

import com.kos.figure.Figure
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import vectors.Vec2
import kotlin.math.abs

data class Corner(
    val center: Vec2,
    val radius:Double,
    val startInDegrees:Double,
    val sweepInDegrees:Double,
):IEdge{
    override fun toFigure():FigureCircle {
        return FigureCircle(
            center = center,
            radius = radius,
            outSide = true,
            segmentStartAngle = Math.toRadians( startInDegrees),
            segmentSweepAngle = Math.toRadians(sweepInDegrees),
        )
    }

    override fun toPath(): IFigurePath  = toFigure()

    override fun perimeter(): Double {
        return abs((sweepInDegrees) * Math.PI / 180.0 * radius)
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        val rot = (startInDegrees + delta * sweepInDegrees) * Math.PI / 180
        val pos = center + Vec2(radius, 0.0).rotate(rot)
        val normal = Vec2(1.0, 0.0).rotate(rot)
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
            outSide = true,
            segmentStartAngle = Math.toRadians(startInDegrees+sweepInDegrees*ste),
            segmentSweepAngle = Math.toRadians(sweepInDegrees*(end-ste))
        )
    }
}