package com.kos.figure.matrix

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2


abstract class FigureMatrix() : IFigure {

    override val count: Int
        get() = 0

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.Empty
    }


    override fun print(): String {
        return ""
    }

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override val hasTransform: Boolean
        get() = true
}

/**
 *       [   1    0    tx  ]
 *       [   0    1    ty  ]
 *       [   0    0    1   ]
 */
class FigureMatrixTranslate(val x: Double, val y: Double) : FigureMatrix() {

    override fun draw(g: IFigureGraphics) {
        g.translate(x, y)
    }

    override fun name(): String {
        return "MatrixTranslate"
    }

    override val transform: Matrix
        get() = Matrix.translate(x, y)
}

class FigureMatrixScale(val x: Double, val y: Double) : FigureMatrix() {
    override fun draw(g: IFigureGraphics) {
        g.scale(x, y)
    }
    override fun name(): String {
        return "MatrixScale"
    }

    override val transform: Matrix
        get() = Matrix.scale(x, y)
}

class FigureMatrixRotate(val angle: Double, val pivot: Vec2 = Vec2.Zero) : FigureMatrix() {
    override fun draw(g: IFigureGraphics) {
        g.rotate(angle, pivot)
    }
    override fun name(): String {
        return "MatrixRotate"
    }

    override val transform: Matrix
        get() {
            val m = Matrix.translate(pivot.x, pivot.y)
            m.rotateX(angle.toFloat())
            m.translate(-pivot.x.toFloat(), -pivot.y.toFloat())
            return m
        }
}


