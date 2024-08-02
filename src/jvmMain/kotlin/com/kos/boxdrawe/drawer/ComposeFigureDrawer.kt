package com.kos.boxdrawe.drawer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.kos.drawer.IFigureGraphics
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs

class ComposeFigureDrawer(
    private val scope: DrawScope,
    var penColor: Color = Color.Gray,
    var style: Stroke = Stroke(width = 1.0f),
    val measurer: TextMeasurer,
) : IFigureGraphics {

    override fun drawLine(a: Vec2, b: Vec2) {
        val p = Path()
        line(p, a, b)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawRect(leftTop: Vec2, size: Vec2) {
        scope.drawRect(
            penColor,
            leftTop.vec,
            Size(size.x.toFloat(), size.y.toFloat()),
            style = style
        )
    }

    override fun drawPolyline(points: List<Vec2>) {
        scope.drawPoints(
            points = pointsFrom(points),
            pointMode = PointMode.Polygon,
            color = penColor,
            strokeWidth = style.width
        )
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

    override fun drawArc(
        center: Vec2,
        radius: Double,
        radiusMinor: Double,
        startAngle: Double,
        sweepAngle: Double
    ) {
        val p = Path()
        drawArc(p, center, radius, radiusMinor, -startAngle, -sweepAngle)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawCircle(center: Vec2, radius: Double) {
        scope.drawCircle(penColor, radius.toFloat(), center.vec, style = style)
    }

    override fun drawSpline(points: List<Vec2>) {
        val p = Path()
        splinePoints(p, points)
        scope.drawPath(p, penColor, style = style)
    }

    override fun drawText(text: String) {
        scope.drawText(
            textMeasurer = measurer,
            text = AnnotatedString(text),
            style = TextStyle.Default.copy(color = penColor)
        )
    }

    override fun save() {
        scope.drawContext.canvas.save()
    }

    override fun translate(x: Double, y: Double) {
        scope.drawContext.canvas.translate(x.toFloat(), y.toFloat())
    }

    override fun scale(scaleX: Double, scaleY: Double) {
        scope.drawContext.canvas.scale(scaleX.toFloat(), scaleY.toFloat())
    }

    override fun rotate(degrees: Double, pivot: Vec2) {
        if (pivot.x == 0.0 && pivot.y == 0.0) {
            scope.drawContext.canvas.rotate(degrees.toFloat())
        } else {
            scope.drawContext.canvas.rotate(degrees.toFloat(), pivot.x.toFloat(), pivot.y.toFloat())
        }
    }

    override fun restore() {
        scope.drawContext.canvas.restore()
    }

    override fun transform(m: vectors.Matrix, actions: () -> Unit) {
        scope.withTransform({ transform(Matrix(m.values)) }) {
            actions()
        }
    }

    override fun setColor(color: Int) {
        penColor = Color(color).copy(alpha = 1.0f)
    }

    override fun getColor(): Int {
        return penColor.toArgb()
    }

    inline val Vec2.vec get(): Offset = Offset(this.x.toFloat(), this.y.toFloat())

    private fun line(p: Path, start: Vec2, end: Vec2) {
        p.moveTo(start.x.toFloat(), start.y.toFloat())
        p.lineTo(end.x.toFloat(), end.y.toFloat())
    }

    private fun pointsFrom(points: List<Vec2>): List<Offset> {
        return points.map { it.vec }
    }

    private fun drawArc(
        p: Path,
        center: Vec2,
        radius: Double,
        radius2: Double,
        startAngle: Double,
        sweepAngle: Double
    ) {
        val rect = Rect(
            (center.x - radius).toFloat(),
            (center.y - radius2).toFloat(),
            (center.x + radius).toFloat(),
            (center.y + radius2).toFloat()
        )
        if (sweepAngle == 0.0 || abs(sweepAngle) >= PI*2)
            p.addOval(rect)
        else
            p.addArcRad(
                rect,
                -startAngle.toFloat(),
                -sweepAngle.toFloat()
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

                p.cubicTo(
                    b.x.toFloat(),
                    b.y.toFloat(),
                    c.x.toFloat(),
                    c.y.toFloat(),
                    d.x.toFloat(),
                    d.y.toFloat()
                )
            }
        }
    }

    private fun splinePoints(p: Path, points: List<Vec2>) {
        if (points.isNotEmpty()) {
            p.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
            for (r in 1..points.size - 3 step 1) {
                val b = points[r]
                val c = points[r + 1]
                val d = points[r + 2]

                p.cubicTo(
                    b.x.toFloat(),
                    b.y.toFloat(),
                    c.x.toFloat(),
                    c.y.toFloat(),
                    d.x.toFloat(),
                    d.y.toFloat()
                )
            }
        }
    }

}