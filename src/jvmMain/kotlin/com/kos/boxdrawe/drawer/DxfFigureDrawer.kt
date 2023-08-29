package com.kos.boxdrawe.drawer

import com.jsevy.jdxf.DXFDocument
import com.jsevy.jdxf.DXFGraphics
import org.jetbrains.skia.defaultLanguageTag
import vectors.Vec2
import java.awt.geom.AffineTransform
import java.util.*
import kotlin.math.PI

class DxfFigureDrawer(
    private val document: DXFDocument
) : IFigureGraphics {

    private val transforms = Stack<AffineTransform>()
    private val g = document.graphics
    override fun drawLine(a: Vec2, b: Vec2) {
        g.drawLine(a.x, a.y, b.x, b.y)
    }
    override fun drawRect(leftTop: Vec2, size: Vec2) {
        g.drawRect(leftTop.x, leftTop.y, size.x, size.y)
    }

    override fun drawPolyline(points: List<Vec2>) {
        g.drawPolyline(points)
    }

    override fun drawBezier(points: List<Vec2>) {
        bezierPoints(g, points)
    }

    override fun drawBezierList(points: List<List<Vec2>>) {
        points.forEach { po ->
            bezierPoints(g, po)
        }
    }

    override fun drawArc(center: Vec2, radius: Double, radiusMinor: Double, startAngle: Double, endAngle: Double) {
        drawArc(g, center, radius, radiusMinor, startAngle, endAngle)
    }

    override fun drawCircle(center: Vec2, radius: Double) {
        g.drawCircle(center, radius)
    }

    override fun drawSpline(points: List<Vec2>) {
        val m = IntArray(points.size) { 1 }
        val controls = DoubleArray(points.size * 2)

        for (i in points.indices) {
            controls[i * 2] = points[i].x
            controls[i * 2 + 1] = points[i].y
        }

        g.drawSpline(3, controls, m, false)
    }

    override fun save() {
        transforms.push(g.transform)
    }

    override fun translate(x: Double, y: Double) {
        g.translate(x, y)
    }

    override fun scale(scaleX: Double, scaleY: Double) {
        g.scale(scaleX, scaleY)
    }

    override fun rotate(degrees: Double, pivot: Vec2) {
        g.rotate(degrees* PI /180.0, pivot.x, pivot.y)
    }

    override fun restore() {
        if (transforms.isNotEmpty()){
            g.transform = transforms.pop()
        }
    }

    private fun drawArc(
        g: DXFGraphics,
        center: Vec2,
        radius: Double,
        radius2: Double,
        startAngle: Double,
        endAngle: Double
    ) {
        if (startAngle == endAngle) {
            if (radius == radius2) {
                g.drawCircle(center, radius)
            } else {
                g.drawOval(
                    (center.x - radius), (center.y - radius2),
                    radius * 2,
                    radius2 * 2,
                )
            }
        } else
            g.drawArc(
                (center.x - radius), (center.y - radius2),
                radius * 2,
                radius2 * 2,
                startAngle,
                (endAngle - startAngle)
            )
    }

    private fun bezierPoints(g: DXFGraphics, points: List<Vec2>) {
        if (points.isNotEmpty()) {
            for (r in 1..points.size - 3 step 3) {
                val a = points[r - 1]
                val b = points[r]
                val c = points[r + 1]
                val d = points[r + 2]
                g.drawBezier(doubleArrayOf(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y))
            }
        }
    }
}