package com.kos.boxdrawe.drawer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import vectors.Vec2

class ComposeFigureDrawer(
    private val scope: DrawScope,
    private val penColor: Color = Color.Gray,
    private val style: DrawStyle = Stroke(width = 1.0f),
) : IFigureGraphics {

    override fun drawLine(a: Vec2, b: Vec2) {
        val p = Path()
        line(p, a, b)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawPolyline(points: List<Vec2>) {
        scope.drawPoints(pointsFrom(points), PointMode.Polygon, penColor)
    }

    override fun drawBezier(points: List<Vec2>) {
        val p = Path()
        bezierPoints(p, points)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawBezierList(points: List<List<Vec2>>) {
        val p = Path()
        bezierMultiPoints(p, points)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawArc(center: Vec2, radius: Double, radiusMinor: Double, startAngle: Double, endAngle: Double) {
        val p = Path()
        drawArc(p, center, radius, radiusMinor, startAngle, endAngle)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawCircle(center: Vec2, radius: Double) {
        scope.drawCircle(penColor, radius.toFloat(), center.vec, style = style)
    }

    inline val Vec2.vec get(): Offset = Offset(this.x.toFloat(), this.y.toFloat())

    private fun line(p: Path, start: Vec2, end: Vec2) {
        p.moveTo(start.x.toFloat(), start.y.toFloat())
        p.lineTo(end.x.toFloat(), end.y.toFloat())
    }

    private fun pointsFrom(points: List<Vec2>): List<Offset> {
        return points.map { it.vec }
    }

    private fun drawArc(p: Path, center: Vec2, radius: Double, radius2: Double, startAngle: Double, endAngle: Double) {
        val rect = Rect(
            (center.x - radius).toFloat(), (center.y - radius2).toFloat(), (center.x + radius).toFloat(),
            (center.y + radius2).toFloat()
        )
        if (startAngle == endAngle)
            p.addOval(rect)
        else
            p.addArc(
                rect,
                -startAngle.toFloat(),
                -(endAngle - startAngle).toFloat()
            )
    }

    private fun bezierMultiPoints(p: Path, points: List<List<Vec2>>) {
        points.forEach { po ->
            bezierPoints(p, po)
        }
    }

    private fun bezierPoints(p: Path, points: List<Vec2>) {
        if (points.isNotEmpty()) {
            p.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
            for (r in 1..points.size - 3 step 3) {
                val b = points[r]
                val c = points[r + 1]
                val d = points[r + 2]

                p.cubicTo(b.x.toFloat(), b.y.toFloat(), c.x.toFloat(), c.y.toFloat(), d.x.toFloat(), d.y.toFloat())
            }
        }
    }

}