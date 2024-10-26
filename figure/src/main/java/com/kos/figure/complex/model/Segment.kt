package com.kos.figure.complex.model

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import vectors.Vec2

interface Segment : PathElement {

    override fun toFigure(): FigureLine {
        return FigureLine(start, end)
    }

    override fun toPath(): IFigurePath = toFigure()

    override fun perimeter(): Double {
        return Vec2.distance(start, end)
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        return PointWithNormal.from(Vec2.lerp(start, end, delta), start, end)
    }

    override fun pointAt(t: Double): Vec2 {
        return Vec2.lerp(start, end, t)
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val d = Vec2.distance(start, end)

        if (d <= 0.0 || endMM<=startMM)
            return FigureEmpty

        val sm = startMM.coerceIn(0.0, d)
        val em = endMM.coerceIn(0.0, d)

        return FigureLine(
            Vec2.lerp(start, end, sm / d),
            Vec2.lerp(start, end, em / d)
        )
    }

    override fun translate(xy: Vec2): Segment {
        return Segment(start = start+xy, end = end+xy)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawLine(start, end)
    }

    companion object {
        operator fun invoke(start: Vec2,end: Vec2): Segment {
            return SegmentImpl(start, end)
        }
    }

}

data class SegmentImpl(
    override val start: Vec2,
    override val end: Vec2,
) : Segment

class SegmentIter(private val points: List<Vec2>, var index: Int) : Segment {
    override val start: Vec2
        get() = points[index]
    override val end: Vec2
        get() = points[index+1]
}