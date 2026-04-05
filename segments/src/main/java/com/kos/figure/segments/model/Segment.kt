package com.kos.figure.segments.model

import com.kos.drawer.IFigureGraphics
import vectors.PointWithNormal
import vectors.Vec2

interface Segment : PathElement {


    override fun perimeter(): Double {
        return Vec2.distance(start, end)
    }

    override fun positionInPath(delta: Double): PointWithNormal {
        return PointWithNormal.from(Vec2.lerp(start, end, delta), start, end)
    }

    override fun pointAt(t: Double): Vec2 {
        return Vec2.lerp(start, end, t)
    }


    override fun translate(xy: Vec2): Segment {
        return Segment(start = start + xy, end = end + xy)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawLine(start, end)
    }

    override fun distance(point: Vec2): Double {
        val l2 = Vec2.distance(start, end).let { it * it }
        if (l2 == 0.0) return Vec2.distance(point, start)

        // Проекция точки на прямую, содержащую отрезок
        var t = ((point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)) / l2
        t = t.coerceIn(0.0, 1.0)

        val projection = Vec2(
            start.x + t * (end.x - start.x),
            start.y + t * (end.y - start.y)
        )
        return Vec2.distance(point, projection)
    }

    companion object {
        operator fun invoke(start: Vec2, end: Vec2): Segment {
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
        get() = points[index + 1]
}