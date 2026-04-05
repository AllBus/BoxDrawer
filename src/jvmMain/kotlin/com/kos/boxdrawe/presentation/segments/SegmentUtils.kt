package com.kos.boxdrawe.presentation.segments

import com.kos.boxdrawe.presentation.model.SegmentBlock
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.complex.transform.toFigure
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.segments.model.Arc
import com.kos.figure.segments.model.Curve
import com.kos.figure.segments.model.Ellipse
import com.kos.figure.segments.model.EmptyPath
import com.kos.figure.segments.model.PathElement
import com.kos.figure.segments.model.Segment
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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

    fun createPathRectangle(allPoints: List<Vec2>): List<PathElement>? {
        if (allPoints.size < 2) return null

        val p1 = allPoints[0]
        val p2 = allPoints[1]
        val diff = p2 - p1
        val width = diff.magnitude
        val angle = diff.angle

        val height = if (allPoints.size >= 3) {
            val p3 = allPoints[2]
            val p3Local = (p3 - p1).rotate(-angle)
            p3Local.y
        } else {
            width * 0.5
        }

        val vWidth = Vec2(width, 0.0).rotate(angle)
        val vHeight = Vec2(0.0, height).rotate(angle)

        val pt1 = p1
        val pt2 = p1 + vWidth
        val pt3 = p1 + vWidth + vHeight
        val pt4 = p1 + vHeight

        return listOf(
            Segment(pt1, pt2),
            Segment(pt2, pt3),
            Segment(pt3, pt4),
            Segment(pt4, pt1)
        )
    }

    fun createPathPolygon(allPoints: List<Vec2>): List<PathElement>? {
        if (allPoints.size < 2) return null

        val center = allPoints[0]
        val p1 = allPoints[1]
        val diff = p1 - center
        val radius = diff.magnitude
        val rotation = diff.angle

        val sides = if (allPoints.size >= 3) {
            val p3 = allPoints[2]
            val p3Local = (p3 - center).rotate(-rotation)
            val d = p3Local.magnitude
            if (d > 0) {
                 (p3Local.angle / (2 * PI) * 100).toInt().coerceIn(3, 100)
            } else 6
        } else {
            6
        }

        val pts = mutableListOf<Vec2>()
        for (i in 0 until sides) {
            val angle = rotation + i * 2 * PI / sides
            pts.add(center + Vec2(radius * cos(angle), radius * sin(angle)))
        }

        val segments = mutableListOf<PathElement>()
        for (i in 0 until sides) {
            segments.add(Segment(pts[i], pts[(i + 1) % sides]))
        }
        return segments
    }


    fun toFigure(segment: PathElement): IFigure {
        return when (segment) {
            is Segment -> segment.toFigure()
            is Arc -> segment.toFigure()
            is Curve -> segment.toFigure()
            is Ellipse -> segment.toFigure()
            else -> FigureEmpty
        }
    }

    fun mapBlock(block: SegmentBlock): IFigure {
        val baseFigure = if (block.isGroup) {
            FigureList(block.children.map { mapBlock(it) })
        } else {
            toFigure(block.element)
        }

        return if (block.matrix.isIdentity()) {
            baseFigure
        } else {
            Figure3dTransform(block.matrix, baseFigure)
        }
    }

    fun getBlockCenter(block: SegmentBlock): Vec2 {
        return if (block.isGroup) {
            val childCenters = block.children.map {
                val c = getBlockCenter(it)

                if (it.matrix.isIdentity()) c else it.matrix.map(c)
            }
            if (childCenters.isEmpty()) return Vec2.Zero
            Vec2(childCenters.map { it.x }.average(), childCenters.map { it.y }.average())
        } else {
            block.element.center
        }
    }

    // 3. Обновляем дистанцию для поиска (hover)
    fun getBlockDistance(block: SegmentBlock, point: Vec2): Double {
        val localPoint = if (block.matrix.isIdentity()) point else block.matrix.getInvert().map(point)
        return if (block.isGroup) {
            block.children.minOfOrNull { getBlockDistance(it, localPoint) } ?: Double.MAX_VALUE
        } else {
            block.element.distance(localPoint)
        }
    }
}
