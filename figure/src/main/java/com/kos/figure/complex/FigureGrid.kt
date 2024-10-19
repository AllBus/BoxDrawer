package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureGrid(
    val columnCount: Int,
    val rowCount: Int,
    val cellWidth: Double,
    val cellHeight: Double,
) : IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(Vec2.Zero, Vec2(cellWidth * columnCount, cellHeight * rowCount))
    }

    override fun draw(g: IFigureGraphics) {
        val fullWidth = cellWidth * columnCount
        val fullHeight = cellHeight * rowCount
        for (i in 0 .. columnCount) {
            g.drawLine(Vec2(cellWidth * i, 0.0), Vec2(cellWidth * i, fullHeight))
        }
        for (i in 0 .. rowCount) {
            g.drawLine(Vec2(0.0, cellHeight * i), Vec2(fullWidth, cellHeight * i))
        }
    }

    override fun print(): String {
        return "/grid $columnCount $rowCount $cellWidth $cellHeight"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "сетка ${columnCount}x$rowCount}"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

}