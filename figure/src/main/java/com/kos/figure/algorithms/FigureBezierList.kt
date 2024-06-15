package com.kos.figure.algorithms

import com.kos.drawer.IFigureGraphics
import com.kos.figure.FigureBezier
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureList
import com.kos.figure.IFigure
import vectors.Vec2

class FigureBezierList(val points: List<List<Vec2>>) {

    constructor(
        a: Vec2, b: Vec2, c: Vec2, d: Vec2
    ) : this(listOf(listOf(a, b, c, d)))

    fun draw(g: IFigureGraphics) {
        g.drawBezierList(points)
    }

    fun print(): String {
        return points.joinToString(" ") { point ->
            var st = point.first()
            "M ${st.x} ${st.y} b ${
                point.drop(1).flatMapIndexed { i, v ->
                    val r = listOf(v.x - st.x, v.y - st.y)
                    if ((i + 1) % 3 == 0) st = v
                    r
                }.joinToString(" ")
            }"
        }
    }

    fun name(): String {
        return "Кривые ${this.points.size}"
    }

    companion object {
        fun simple(beziers: List<FigureBezierList>): FigureBezierList {
            val b = beziers.flatMap { it.points }.filter { it.isNotEmpty() }
            val list = ArrayList<List<Vec2>>()

            var current: List<Vec2>? = null

            for (a in b) {
                if (current != null) {
                    if (current.last() == a.first()) {
                        current = current + a.drop(1)
                    } else {
                        list.add(current)
                        current = a
                    }
                } else {
                    current = a
                }
            }
            current?.let { list.add(it) }

            return FigureBezierList(list)
        }

    }

    fun toFigure(): IFigure {
        if (points.size == 0)
            return FigureEmpty
        if (points.size == 1)
            return FigureBezier(points[0])
        else
            return FigureList(points.filter { it.size>=4 }.map { p -> FigureBezier(p) })
    }
}