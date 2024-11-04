package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.BaseFigure
import com.kos.figure.Figure
import com.kos.figure.IFigure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureImage(val origin:Vec2, val uri:String,  val size: Vec2) : BaseFigure(){
    override val count: Int
        get() = 0

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(origin, origin+size)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawImage(origin, size, uri)
    }

    override fun print(): String {
        return "/image ($uri)"
    }

    override fun name(): String {
        return "Картинка"
    }
}