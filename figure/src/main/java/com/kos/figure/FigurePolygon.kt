package com.kos.figure

import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

abstract class FigurePolygon(val points: List<Vec2>) : Figure(), IFigurePath {
    override fun rect(): BoundingRectangle {
        return if (points.size < 2)
            BoundingRectangle(Vec2(0.0, 0.0), 0.0)
        else
            BoundingRectangle.apply(points)
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        if (points.size < 2) {
            return FigureEmpty
        }

        val predicate = when (cropSide) {
            CropSide.LEFT ->
                {a: Vec2 -> a.x > k}
            CropSide.RIGHT ->
                {a -> a.x < k}
            CropSide.BOTTOM ->
                {a -> a.y > k}
            CropSide.TOP ->
                {a -> a.y < k}
        }//end when

        if (points.all { !predicate(it) })
            return FigureEmpty

        return this
    }

    abstract fun create(points: List<Vec2>): FigurePolygon

    override fun translate(translateX: Double, translateY: Double): FigurePolygon {
        return create(points.map { p ->
            Vec2(
                p.x + translateX,
                p.y + translateY
            )
        })
    }

    override fun rotate(angle: Double): FigurePolygon {
        return create(points.map { p ->
            p.rotate(angle)
        })
    }

    override fun transform(matrix: Matrix): FigurePolygon {
        return create(points.map { p ->
            matrix.map(p)
        })
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): FigurePolygon {
        return create(points.map { p ->
            (p+rotateCenter).rotate(angle)-rotateCenter
        })
    }

    override fun startPoint(): Vec2 {
        return points.firstOrNull()?:Vec2.Zero
    }

    override fun endPoint(): Vec2 {
        return points.lastOrNull()?:Vec2.Zero
    }

    override fun toFigure(): Figure = this
}