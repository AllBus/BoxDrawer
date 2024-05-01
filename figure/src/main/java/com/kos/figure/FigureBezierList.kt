package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2

class FigureBezierList(val points: List<List<Vec2>>) : Figure() {

    constructor(
        a: Vec2, b: Vec2, c: Vec2, d: Vec2
    ) : this(listOf(listOf(a, b, c, d)))

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        val figures = mutableListOf<List<Vec2>>()

        val (predicate, napr) = when (cropSide) {
            CropSide.LEFT ->
                Pair({ a: Vec2 -> a.x >= k }, { a: Vec2 -> a.x })

            CropSide.RIGHT ->
                Pair({ a: Vec2 -> a.x <= k }, { a: Vec2 -> a.x })

            CropSide.BOTTOM ->
                Pair({ a: Vec2 -> a.y >= k }, { a: Vec2 -> a.y })

            CropSide.TOP ->
                Pair({ a: Vec2 -> a.y <= k }, { a: Vec2 -> a.y })
        }//end when

        for (ps in points) {
            when {
                ps.all(predicate) -> figures.add(ps)
                !ps.all { !predicate(it) } -> {
                    val roots = Vec2.getCubicRoots(ps.map(napr).map { it - k })
                    //Todo: Правильно фильтровать
                    figures.addAll(Vec2.casteljauLine(ps, roots).filter { l -> predicate(l[1]) })
                }
            }
        }

        if (figures.isEmpty())
            return Empty
        return FigureBezierList(figures.toList())
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.union(points.map(BoundingRectangle.Companion::apply))
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureBezierList(points.map { l ->
            l.map { p ->
                Vec2(
                    p.x + translateX,
                    p.y + translateY
                )
            }
        })
    }

    override fun rotate(angle: Double): IFigure {
        return FigureBezierList(points.map { l -> l.map { p -> p.rotate(angle) } })
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureBezierList(points.map { l -> l.map { p -> (p - rotateCenter).rotate(angle) + rotateCenter } })
    }

    override fun draw(g: IFigureGraphics) {
        g.drawBezierList(points)
    }

    override fun print(): String {
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

    override fun name(): String {
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

}