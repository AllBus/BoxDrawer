package com.kos.figure

import com.kos.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2


interface IFigure {

    val count: Int
    fun crop(k: Double, cropSide: CropSide): IFigure
    fun list(): List<Figure>
    fun rect(): BoundingRectangle

    fun translate(translateX: Double, translateY: Double): IFigure
    fun rotate(angle: Double): IFigure
    fun rotate(angle: Double, rotateCenter: Vec2): IFigure
    fun draw(g: IFigureGraphics)

    fun print():String
    fun collection(): List<IFigure>
    fun name():String

    companion object {
        fun list(figure:IFigure):List<IFigure>{
            return listOf(figure)+figure.collection().flatMap { list(it) }
        }

    }
}

enum class CropSide {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
}

abstract class Figure : IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return listOf(this)
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String {
        return this.javaClass.name
    }

    companion object {
        val Empty = FigureEmpty
    }
}

interface Approximation {
    fun approximate(pointCount: Int): List<List<Vec2>>
}

object FigureEmpty: IFigure {
    override val count: Int
        get() = 0

    override fun crop(k: Double, cropSide: CropSide): IFigure = this

    override fun list(): List<Figure> = emptyList()

    override fun rect(): BoundingRectangle = BoundingRectangle.Empty

    override fun translate(translateX: Double, translateY: Double): IFigure = this

    override fun rotate(angle: Double): IFigure = this

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure = this

    override fun draw(g: IFigureGraphics) {}
    override fun print(): String {
        return ""
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String = "Empty"
}