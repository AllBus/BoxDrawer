package com.kos.boxdrawe.drawer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import com.jsevy.jdxf.DXFDocument
import com.jsevy.jdxf.DXFGraphics
import figure.*
import vectors.Vec2

object FigureDxf {

   fun draw(document: DXFDocument,  figureLine: IFigure){

       val g = document.graphics
       figureLine.list().forEach {f ->

           when (f) {
               is FigureLine ->
                   g.drawLine(f.points[0].x, f.points[0].y, f.points[1].x, f.points[1].y)
               is FigurePolyline ->
                   g.drawPolyline(f.points)
               is FigureCircle -> {
                   arc(g, f.center, f.radius, f.radiusMinor, f.segmentStart, f.segmentEnd)
               }
               is FigureBezier -> {
                   bezierPoints(g, f.points)
               }
               is FigureBezierList -> {
                   bezierMultiPoints(g, f.points)
               }
           }
       }
   }
}

private fun arc(g: DXFGraphics, center: Vec2, radius: Double, radius2: Double, startAngle: Double, endAngle: Double) {
    if (startAngle == endAngle) {
        if (radius == radius2){
            g.drawCircle(center, radius)
        }else{
            g.drawOval( (center.x - radius), (center.y - radius2),
                radius*2,
                radius2*2,)
        }
    }
    else
        g.drawArc(
            (center.x - radius), (center.y - radius2),
            radius*2,
            radius2*2,
            startAngle,
            (endAngle - startAngle)
        )
}

private fun bezierMultiPoints(g: DXFGraphics, points: List<List<Vec2>>) {
    points.forEach { po ->
        bezierPoints(g, po)
    }
}

private fun bezierPoints(g: DXFGraphics, points: List<Vec2>) {
    if (points.isNotEmpty()) {
        for (r in 1..points.size - 3 step 3) {
            val a = points[r-1]
            val b = points[r]
            val c = points[r + 1]
            val d = points[r + 2]
            g.drawBezier(doubleArrayOf(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y))
        }
    }
}