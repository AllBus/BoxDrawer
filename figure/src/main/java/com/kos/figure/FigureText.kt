package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigureText(val text:String, transform : Matrix = Matrix.identity): Figure() {
    override val count: Int
        get() = 1

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        // TODO
        return this
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(transform.map(Vec2.Zero) , transform.map(Vec2.Zero) )
    }

    override fun translate(translateX: Double, translateY: Double): FigureText {
        val m = Matrix()
        m.setFrom(transform)
        m.translate(translateX.toFloat(), translateY.toFloat())
        return FigureText(text, m)
    }

    override fun rotate(angle: Double): FigureText {
        val m = Matrix()
        m.setFrom(transform)
        m.rotateZ(angle.toFloat())
        return FigureText(text, m)
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): FigureText {
        val m = Matrix()
        m.setFrom(transform)
        m.translate(rotateCenter.x.toFloat(), rotateCenter.y.toFloat())
        m.rotateZ(angle.toFloat())
        m.translate(-rotateCenter.x.toFloat(), -rotateCenter.y.toFloat())
        return FigureText(text, m)
    }

    override fun transform(matrix: Matrix): FigureText {
        val m = Matrix()
        m.setFrom(transform)
        m.timesAssign(matrix)
        return FigureText(text, m)
    }

    override fun draw(g: IFigureGraphics) {
        g.transform(transform) {
            g.drawText(text)
        }
    }

    override fun print(): String {
        return "/print [$text]"
    }

    override fun name(): String {
        return "Text"
    }


}