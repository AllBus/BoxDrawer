package com.kos.boxdrawe.presentation.segments

import com.kos.figure.segments.model.Arc
import com.kos.figure.segments.model.Arc.Companion.invoke
import com.kos.figure.segments.model.Curve
import com.kos.figure.segments.model.Curve.Companion.invoke
import com.kos.figure.segments.model.Ellipse
import com.kos.figure.segments.model.Ellipse.Companion.invoke
import com.kos.figure.segments.model.PathElement
import com.kos.figure.segments.model.Segment
import com.kos.figure.segments.model.Segment.Companion.invoke
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object SegmentUtils {
    fun createPathLine(allPoints: List<Vec2>): Segment? =
        if (allPoints.size >= 2) Segment(allPoints[0], allPoints[1]) else null

    fun createPathCircle(allPoints: List<Vec2>): Arc? = if (allPoints.size >= 2) {
        val radius = Vec2.distance(allPoints[0], allPoints[1])
        if (radius > 0) {
            Arc(
                center = allPoints[0],
                radius = radius,
                outSide = true,
                startAngle = 0.0,
                sweepAngle = PI * 2
            )
        } else null
    } else null

    fun createPathBezier(allPoints: List<Vec2>): PathElement? = when (allPoints.size) {
        2 -> Segment(allPoints[0], allPoints[1])
        3 -> {
            val p0 = allPoints[0]
            val p1 = allPoints[1]
            val p2 = allPoints[2]
            // Точный перевод квадратичной кривой в кубическую
            Curve(
                p0,
                p0 + (p1 - p0) * (2.0 / 3.0),
                p2 + (p1 - p2) * (2.0 / 3.0),
                p2
            )
        }
        4 -> Curve(allPoints[0], allPoints[1], allPoints[2], allPoints[3])
        else -> null
    }

    fun createPathEllipse(allPoints: List<Vec2>): Ellipse? = if (allPoints.size >= 2) {
        val center = allPoints[0]
        val p1 = allPoints[1]
        val diff = p1 - center
        val radiusX = diff.magnitude
        val rotation = atan2(diff.y, diff.x)

        val radiusY = if (allPoints.size >= 3) {
            val p2 = allPoints[2]
            // Проекция p2 на перпендикулярную ось
            val p2Local = (p2 - center).rotate(-rotation)
            abs(p2Local.y)
        } else {
            radiusX * 0.5 // Превью второго радиуса
        }

        if (radiusX > 0 && radiusY > 0) {
            Ellipse(
                center = center,
                radiusX = radiusX,
                radiusY = radiusY,
                rotation = rotation,
                startAngle = 0.0,
                endAngle = PI * 2,
                outSide = true
            )
        } else null
    } else null
}