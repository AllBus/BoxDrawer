package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureImage(val origin:Vec2, val uri:String,  val size: Vec2) : IFigure{
    override val count: Int
        get() = 0

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(origin, origin+size)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawImage(origin, size, uri)
    }

    override fun print(): String {
        return "/image ($uri)"
    }

    override fun collection(): List<IFigure> {
        TODO("Not yet implemented")
    }

    override fun name(): String {
        return "Картинка"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false
}