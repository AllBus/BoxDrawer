package com.kos.figure

import com.kos.drawer.IFigureGraphics
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import vectors.BoundingRectangle
import vectors.Vec2

class FigureText(val text:String): Figure() {
    override val count: Int
        get() = 1

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        // TODO
        return this
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(Vec2.Zero, Vec2.Zero)
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureTranslate(this, Vec2(translateX, translateY))
    }

    override fun rotate(angle: Double): IFigure {
        return FigureRotate(this, angle, Vec2.Zero)
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureRotate(this, angle, rotateCenter)
    }

    override fun draw(g: IFigureGraphics) {
        g.drawText(text)
    }

    override fun print(): String {
        return "/print [$text]"
    }

    override fun name(): String {
        return "Text"
    }


}