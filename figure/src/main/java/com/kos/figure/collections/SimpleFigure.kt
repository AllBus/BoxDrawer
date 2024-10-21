package com.kos.figure.collections

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class SimpleFigure(val figures:List<Figure>): Figure() {
    override fun translate(translateX: Double, translateY: Double): Figure {
        return SimpleFigure(figures.map { it.translate(translateX, translateY) })
    }
//
//    override fun rotate(angle: Double): Figure {
//        return SimpleFigure(figures.map { it.rotate(angle) })
//    }
//
//    override fun rotate(angle: Double, rotateCenter: Vec2): Figure {
//        return SimpleFigure(figures.map { it.rotate(angle, rotateCenter) })
//    }

    override fun transform(matrix: Matrix): Figure {
        return SimpleFigure(figures.map { it.transform(matrix) })
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return SimpleFigure(figures.map { it.crop(k, cropSide) })
    }

    override val count: Int
        get() = figures.size

    override fun collection(): List<IFigure> {
        return figures
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.union(figures.map { it.rect() })
    }

    override fun draw(g: IFigureGraphics) {
        figures.forEach { it.draw(g) }
    }

    override fun print(): String {
        return figures.joinToString(" ") { it.print() }
    }

    override fun name(): String {
        return "Список фигур"
    }
}