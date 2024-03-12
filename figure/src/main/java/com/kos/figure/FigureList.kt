package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2

class FigureList(
    private val figures: List<IFigure>
) : IFigure {

    override val count: Int
        get() = figures.size

    override fun list(): List<Figure> {
        return figures.flatMap { it.list() }
    }

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return FigureList(
            figures.map { it.crop(k, cropSide) }
        )
    }

    override fun rect(): BoundingRectangle {
        val l = figures.map { it.rect() }

        if (l.isEmpty()) {
            return BoundingRectangle.Empty
        }

        return l.fold(l.first()) { a, b -> a.union(b) }
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureList(figures.map { it.translate(translateX, translateY) })
    }

    override fun rotate(angle: Double): IFigure {
        return FigureList(figures.map { it.rotate(angle) })
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureList(figures.map { it.rotate(angle, rotateCenter) })
    }

    override fun draw(g: IFigureGraphics) {
        figures.forEach { it.draw(g) }
    }

    fun simple(): FigureList {
        return FigureList(list())
    }

    override fun print(): String {
        return figures.joinToString(" ") { it.print() }
    }
}