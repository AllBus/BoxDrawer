package com.kos.boxdrawe.drawer

import com.jsevy.jdxf.DXFDocument
import com.jsevy.jdxf.DXFGraphics
import vectors.Vec2

class DxfFigureDrawer(
    private val document: DXFDocument
) : IFigureGraphics {
    private val g = document.graphics
    override fun drawLine(a: Vec2, b: Vec2) {
        g.drawLine(a.x, a.y, b.x, b.y)
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