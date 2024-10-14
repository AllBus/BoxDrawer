package com.kos.drawer

import vectors.Matrix
import vectors.Vec2

interface IFigureGraphics {

    fun drawLine(a: Vec2, b: Vec2)

    fun drawRect(leftTop: Vec2, size: Vec2)

    fun drawPolyline(points: List<Vec2>)

    fun drawBezier(points: List<Vec2>)
    fun drawBezierList(points: List<List<Vec2>>)

    /** inRadians */
    fun drawArc(center: Vec2, radius: Double, radiusMinor: Double, startAngle: Double, sweepAngle: Double)

    fun drawCircle(center: Vec2, radius: Double)

    fun drawSpline(points: List<Vec2>)
    fun drawText(text:String)
    fun save()
    fun translate(x: Double, y: Double)
    fun scale(scaleX: Double, scaleY: Double)
    fun rotate(degrees: Double, pivot: Vec2)

    fun transform(m: Matrix, actions: () -> Unit)
    fun restore()

    fun setColor(color:Int)

    fun getColor():Int

    fun isSimple():Boolean

    fun setSimple(value:Boolean)
}